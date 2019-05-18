package com.lig.libby.config;

import com.lig.libby.config.util.StringBigIntegerConverter;
import com.lig.libby.config.util.StringIntegerConverter;
import com.lig.libby.config.util.StringStringConverter;
import com.lig.libby.domain.*;
import com.lig.libby.domain.core.AbstractPersistentObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableBatchProcessing
@SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
public class BatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public static FlatFileItemReader<BookMigration> workBookCsvToWorkBookReader() {
        BeanWrapperFieldSetMapper<BookMigration> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(BookMigration.class);
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new StringBigIntegerConverter());
        defaultConversionService.addConverter(new StringIntegerConverter());
        defaultConversionService.addConverter(new StringStringConverter());
        mapper.setConversionService(defaultConversionService);

        return new FlatFileItemReaderBuilder<BookMigration>()
                //.strict(true)
                .linesToSkip(1)
                .name("workBookCsvToWorkBookReader")
                .resource(new ClassPathResource("/db/changelog/data/data/work_book_denormalized.csv"))
                .delimited()
                .names(new String[]{
                        AbstractPersistentObject.Fields.id,
                        BookMigration.Fields.bookId,
                        BookMigration.Fields.bestBookId,
                        BookMigration.Fields.workId,
                        BookMigration.Fields.booksCount,
                        BookMigration.Fields.isbn,
                        BookMigration.Fields.isbn13,
                        BookMigration.Fields.authors,
                        BookMigration.Fields.originalPublicationYear,
                        BookMigration.Fields.originalTitle,
                        BookMigration.Fields.title,
                        BookMigration.Fields.languageCode,
                        BookMigration.Fields.averageRating,
                        BookMigration.Fields.ratingsCount,
                        BookMigration.Fields.workRatingsCount,
                        BookMigration.Fields.workTextReviewsCount,
                        BookMigration.Fields.ratings1,
                        BookMigration.Fields.ratings2,
                        BookMigration.Fields.ratings3,
                        BookMigration.Fields.ratings4,
                        BookMigration.Fields.ratings5,
                        BookMigration.Fields.imageUrl,
                        BookMigration.Fields.smallImageUrl
                })
                .fieldSetMapper(mapper)
                .build();
    }

    @Bean
    public TaskExecutor springBatchTaskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public ItemProcessor<BookMigration, BookMigration> bookDoNothingProcessor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    public JpaItemWriter<BookMigration> bookMigrationJpaItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<BookMigration> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepBookCSVLoadToMigrationTable(JpaItemWriter<BookMigration> writer, TaskExecutor springBatchTaskExecutor, FlatFileItemReader<BookMigration> workBookCsvToWorkBookReader, ItemProcessor<BookMigration, BookMigration> bookDoNothingProcessor) {
        return stepBuilderFactory.get("stepBookCSVLoadToMigrationTable")
                .<BookMigration, BookMigration>chunk(500)
                .reader(workBookCsvToWorkBookReader)
                .processor(bookDoNothingProcessor)
                .writer(writer)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }

    @Bean
    @SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
    public Tasklet insertWorkWithoutBestBookIdFromBookMigrationTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            new JdbcTemplate(dataSource)
                    .execute(
                            "INSERT INTO " + Work.TABLE +
                                    "( " + Work.ID_COLUMN +
                                    ") " +
                                    " select " +
                                    "  bm." + BookMigration.ID_COLUMN +
                                    " from " + BookMigration.TABLE + " bm "
                    );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepBookMigrationLoadToWorkWithoutBestBookId(Tasklet insertWorkWithoutBestBookIdFromBookMigrationTasklet) {
        return this.stepBuilderFactory.get("stepRatingMigrationTableLoadToUserAndUserAuthorityDomainTable")
                .tasklet(insertWorkWithoutBestBookIdFromBookMigrationTasklet)
                .build();
    }

    @Bean
    public Tasklet insertBookFromBookMigrationTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            new JdbcTemplate(dataSource)
                    .execute(
                            "INSERT INTO " + Book.TABLE +
                                    "( " + Book.ID_COLUMN +
                                    ", " + Book.Columns.ISBN +
                                    ", " + Book.Columns.ISBN13 +
                                    ", " + Book.Columns.AUTHORS +
                                    ", " + Book.Columns.ORIGINAL_PUBLICATION_YEAR +
                                    ", " + Book.Columns.ORIGINAL_TITLE +
                                    ", " + Book.Columns.TITLE +
                                    ", " + Book.Columns.IMAGE_URL +
                                    ", " + Book.Columns.SMALL_IMAGE_URL +
                                    ", " + Book.Columns.NAME +
                                    ", " + Book.Columns.LANG_ID +
                                    ", " + Book.Columns.WORK_ID +
                                    ") " +
                                    " select " +
                                    "  bm." + BookMigration.Columns.BOOK_ID +
                                    ", bm." + BookMigration.Columns.ISBN +
                                    ", bm." + BookMigration.Columns.ISBN13 +
                                    ", bm." + BookMigration.Columns.AUTHORS +
                                    ", bm." + BookMigration.Columns.ORIGINAL_PUBLICATION_YEAR +
                                    ", bm." + BookMigration.Columns.ORIGINAL_TITLE +
                                    ", bm." + BookMigration.Columns.TITLE +
                                    ", bm." + BookMigration.Columns.IMAGE_URL +
                                    ", bm." + BookMigration.Columns.SMALL_IMAGE_URL +
                                    ", bm." + BookMigration.Columns.NAME +
                                    ", bm." + BookMigration.Columns.LANGUAGE_CODE +
                                    ", bm." + BookMigration.ID_COLUMN +
                                    " from " + BookMigration.TABLE + " bm "
                    );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepBookMigrationLoadToBook(Tasklet insertBookFromBookMigrationTasklet) {
        return this.stepBuilderFactory.get("stepBookMigrationLoadToBook")
                .tasklet(insertBookFromBookMigrationTasklet)
                .build();
    }

    @Bean
    public FlatFileItemReader<RatingMigration> ratingCsvToRatingMigrationReader(@Value("${migrationFromKaggleDataSetJob.ratingSkipLines}") Integer ratingSkipLines) {
        BeanWrapperFieldSetMapper<RatingMigration> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(RatingMigration.class);

        return new FlatFileItemReaderBuilder<RatingMigration>()
                .linesToSkip(Optional.ofNullable(ratingSkipLines).orElse(1))
                .name("ratingCsvToRatingMigrationReader")
                .resource(new ClassPathResource("/db/changelog/data/data/ratings.csv"))
                .delimited()
                .names(new String[]{
                        RatingMigration.Fields.bookId,
                        RatingMigration.Fields.userId,
                        RatingMigration.Fields.rating
                })
                .fieldSetMapper(mapper)
                .build();
    }


    @Bean
    public ItemProcessor<RatingMigration, RatingMigration> ratingDoNothingProcessor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    public JpaItemWriter<RatingMigration> ratingMigrationJpaItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<RatingMigration> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepRatingCSVLoadToMigrationTable(JpaItemWriter<RatingMigration> jpaItemWriter, TaskExecutor springBatchTaskExecutor, FlatFileItemReader<RatingMigration> ratingCsvToRatingMigrationReader, ItemProcessor<RatingMigration, RatingMigration> ratingDoNothingProcessor) {
        return stepBuilderFactory.get("stepRatingCSVLoadToMigrationTable")
                .<RatingMigration, RatingMigration>chunk(5000)
                .reader(ratingCsvToRatingMigrationReader)
                .processor(ratingDoNothingProcessor)
                .writer(jpaItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }


    @Bean
    public Tasklet insertUserAndAuthoritiesFromRatingTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            new JdbcTemplate(dataSource)
                    .execute(
                            "INSERT INTO " + User.TABLE +
                                    " ( " + User.ID_COLUMN +
                                    " , " + User.Columns.NAME_COLUMN +
                                    " , " + User.Columns.EMAIL_COLUMN +
                                    " , " + User.Columns.EMAIL_VERIFIED_COLUMN +
                                    " ) " +
                                    " select " +
                                    "   DU.d_user_id " +
                                    " , DU.d_user_id " +
                                    " , DU.d_user_id " +
                                    " , true         " +
                                    " from (select distinct( " + RatingMigration.Columns.USER_ID_COLUMN + ") as d_user_id from " + RatingMigration.TABLE + ") DU; "
                                    +
                                    " INSERT INTO " + User.USER_AUTHORITY_JOIN_TABLE +
                                    " ( " + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN +
                                    " , " + User.USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN +
                                    " ) " +
                                    " select " +
                                    "  DU.d_user_id " +
                                    " ,(select max(  " + Authority.ID_COLUMN + ") " +
                                    "          from  " + Authority.TABLE +
                                    "          where " + Authority.NAME_COLUMN + " = '" + Authority.Roles.USER + "'" +
                                    "  )" +
                                    " from (select distinct( " + RatingMigration.Columns.USER_ID_COLUMN + ") as d_user_id from " + RatingMigration.TABLE + ") DU"
                    );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepRatingMigrationTableLoadToUserAndUserAuthorityDomainTable(Tasklet insertUserAndAuthoritiesFromRatingTasklet) {
        return this.stepBuilderFactory.get("stepRatingMigrationTableLoadToUserAndUserAuthorityDomainTable")
                .tasklet(insertUserAndAuthoritiesFromRatingTasklet)
                .build();
    }


    @Bean
    public Tasklet insertCommentFromRatingTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            new JdbcTemplate(dataSource)
                    .execute(
                            "INSERT INTO " + Comment.TABLE +
                                    "( " + Comment.ID_COLUMN +
                                    ", " + Comment.Columns.BOOK_ID_COLUMN +
                                    ", " + Comment.CREATED_BY_COLUMN +
                                    ", " + Comment.Columns.RATING_COLUMN +
                                    ") " +
                                    " select " +
                                    "  r." + RatingMigration.ID_COLUMN +
                                    ", b." + Book.ID_COLUMN +
                                    ", r." + RatingMigration.Columns.USER_ID_COLUMN +
                                    ", r." + RatingMigration.Columns.RATING_COLUMN +
                                    " from " + RatingMigration.TABLE + " r " +
                                    " left join " + Book.TABLE + " b " + "on r." + RatingMigration.Columns.BOOK_ID_COLUMN + " = b." + Book.Columns.WORK_ID
                    );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepRatingMigrationTableLoadToCommentDomainTable(Tasklet insertCommentFromRatingTasklet) {
        return this.stepBuilderFactory.get("stepRatingMigrationTableLoadToCommentDomainTable")
                .tasklet(insertCommentFromRatingTasklet)
                .build();
    }


    @Bean
    public Job importUserAuthorityAndCommentsFromRatingJob(Step stepBookCSVLoadToMigrationTable, Step stepBookMigrationLoadToWorkWithoutBestBookId, Step stepBookMigrationLoadToBook, Step stepRatingCSVLoadToMigrationTable, Step stepRatingMigrationTableLoadToUserAndUserAuthorityDomainTable, Step stepRatingMigrationTableLoadToCommentDomainTable) {
        return jobBuilderFactory.get("migrationFromKaggleDataSetJob")
                .incrementer(new RunIdIncrementer())
                .start(stepBookCSVLoadToMigrationTable)
                .next(stepBookMigrationLoadToWorkWithoutBestBookId)
                .next(stepBookMigrationLoadToBook)
                .next(stepRatingCSVLoadToMigrationTable)
                .next(stepRatingMigrationTableLoadToUserAndUserAuthorityDomainTable)
                .next(stepRatingMigrationTableLoadToCommentDomainTable)
                .build();
    }

}