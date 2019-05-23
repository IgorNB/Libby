package com.lig.libby.config;

import com.lig.libby.config.util.StringBigIntegerConverter;
import com.lig.libby.config.util.StringIntegerConverter;
import com.lig.libby.config.util.StringStringConverter;
import com.lig.libby.domain.*;
import com.lig.libby.domain.core.AbstractPersistentObject;
import com.lig.libby.repository.BookRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public ItemProcessor<BookMigration, Work> bookMigrationToWorkProcessor() {
        return item -> {
            Work work = new Work();
            work.setId(item.getId());
            return work;
        };
    }

    @Bean
    public MongoItemWriter<Work> workMongoItemWriter(MongoTemplate entityManagerFactory) {
        MongoItemWriter<Work> writer = new MongoItemWriter<>();
        writer.setTemplate(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepInsertWorkWithoutBestBookIdFromBookMigration(MongoItemWriter<Work> bookMigrationMongoItemWriter, TaskExecutor springBatchTaskExecutor, FlatFileItemReader<BookMigration> workBookCsvToWorkBookReader, ItemProcessor<BookMigration, Work> workMongoItemWriter) {
        return stepBuilderFactory.get("stepInsertWorkWithoutBestBookIdFromBookMigration")
                .<BookMigration, Work>chunk(500)
                .reader(workBookCsvToWorkBookReader)
                .processor(workMongoItemWriter)
                .writer(bookMigrationMongoItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }

    @Bean
    public ItemProcessor<BookMigration, Book> bookMigrationToBookProcessor() {
        return item -> {
            Book book = new Book();
            book.setId(item.getBookId());
            book.setIsbn(item.getIsbn());
            book.setIsbn13(item.getIsbn13());
            book.setAuthors(item.getAuthors());
            book.setOriginalPublicationYear(item.getOriginalPublicationYear());
            book.setOriginalTitle(item.getOriginalTitle());
            book.setTitle(item.getTitle());
            book.setImageUrl(item.getImageUrl());
            book.setSmallImageUrl(item.getSmallImageUrl());
            book.setName(item.getName());
            if (item.getLanguageCode() != null) {
                Lang lang = new Lang();
                lang.setId(item.getLanguageCode());
                book.setLang(lang);
            }

            Work work = new Work();
            work.setId(item.getWorkId());
            book.setWork(work);
            return book;
        };
    }

    @Bean
    public MongoItemWriter<Book> bookMongoItemWriter(MongoTemplate entityManagerFactory) {
        MongoItemWriter<Book> writer = new MongoItemWriter<>();
        writer.setTemplate(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepInsertBookFromBookMigration(MongoItemWriter<Book> bookMigrationMongoItemWriter, TaskExecutor springBatchTaskExecutor, FlatFileItemReader<BookMigration> workBookCsvToWorkBookReader, ItemProcessor<BookMigration, Book> bookMigrationToBookProcessor) {
        return stepBuilderFactory.get("stepInsertBookFromBookMigration")
                .<BookMigration, Book>chunk(500)
                .reader(workBookCsvToWorkBookReader)
                .processor(bookMigrationToBookProcessor)
                .writer(bookMigrationMongoItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
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
    public JdbcBatchItemWriter<RatingMigration> ratingMigrationJdbcItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<RatingMigration>()
                .dataSource(dataSource)
                .sql("INSERT INTO " + RatingMigration.TABLE +
                        " (" +
                        " " + RatingMigration.ID_COLUMN +
                        " ," + RatingMigration.Columns.BOOK_ID_COLUMN +
                        " ," + RatingMigration.Columns.USER_ID_COLUMN +
                        " ," + RatingMigration.Columns.RATING_COLUMN +
                        " )" +
                        " VALUES " +
                        " ( " +
                        "   :" + AbstractPersistentObject.Fields.id +
                        " , :" + RatingMigration.Fields.bookId +
                        " , :" + RatingMigration.Fields.userId +
                        " , :" + RatingMigration.Fields.rating +
                        " ) ")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider())
                .build();
    }

    @Bean
    public Step stepRatingCSVLoadToMigrationTable(JdbcBatchItemWriter<RatingMigration> ratingMigrationJdbcItemWriter, TaskExecutor springBatchTaskExecutor, FlatFileItemReader<RatingMigration> ratingCsvToRatingMigrationReader, ItemProcessor<RatingMigration, RatingMigration> ratingDoNothingProcessor) {
        return stepBuilderFactory.get("stepRatingCSVLoadToMigrationTable")
                .<RatingMigration, RatingMigration>chunk(5000)
                .reader(ratingCsvToRatingMigrationReader)
                .processor(ratingDoNothingProcessor)
                .writer(ratingMigrationJdbcItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<User> ratingMigrationToUserReader(DataSource dataSource) {
        return new JdbcPagingItemReaderBuilder<User>()
                .dataSource(dataSource)
                .selectClause("select d_user_id")
                .fromClause(
                        "from (  " +
                                "select distinct( " + RatingMigration.Columns.USER_ID_COLUMN + ") as d_user_id from " + RatingMigration.TABLE + ") t ")
                .whereClause("where d_user_id is not null ")
                .sortKeys(new HashMap<String, Order>() {{
                    put("d_user_id", Order.ASCENDING);
                }})
                .rowMapper((resultSet, i) -> {
                    User user = new User();
                    user.setId(resultSet.getString("d_user_id"));
                    user.setName(user.getId());
                    user.setEmail(user.getId());
                    user.setEmailVerified(true);
                    Authority userAuthority = new Authority();
                    userAuthority.setId("0-3");
                    Set<Authority> auths = new HashSet<>();
                    auths.add(userAuthority);
                    user.setAuthorities(auths);
                    return user;
                })
                .saveState(false)
                .build()
                ;
    }

    @Bean
    public ItemProcessor<User, User> userDoNothingProcessor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    public MongoItemWriter<User> userMongoItemWriter(MongoTemplate entityManagerFactory) {
        MongoItemWriter<User> writer = new MongoItemWriter<>();
        writer.setTemplate(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepRatingMigrationLoadToUserTable(MongoItemWriter<User> userMongoItemWriter, TaskExecutor springBatchTaskExecutor, JdbcPagingItemReader<User> ratingMigrationToUserReader, ItemProcessor<User, User> userDoNothingProcessor) {
        return stepBuilderFactory.get("stepRatingMigrationLoadToUserTable")
                .<User, User>chunk(5000)
                .reader(ratingMigrationToUserReader)
                .processor(userDoNothingProcessor)
                .writer(userMongoItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Comment> ratingMigrationToCommentReader(DataSource dataSource, BookRepository repository) {
        return new JdbcPagingItemReaderBuilder<Comment>()
                .dataSource(dataSource)
                .selectClause(" select " +
                        "  " + RatingMigration.ID_COLUMN + " as " + Comment.ID_COLUMN +
                        ", " + RatingMigration.Columns.BOOK_ID_COLUMN + " as " + Book.Columns.WORK_ID +
                        ", " + RatingMigration.Columns.USER_ID_COLUMN + " as " + Comment.CREATED_BY_COLUMN +
                        ", " + RatingMigration.Columns.RATING_COLUMN + " as " + Comment.Columns.RATING_COLUMN)
                .fromClause(
                        " from " + RatingMigration.TABLE + " r ")
                .sortKeys(new HashMap<String, Order>() {{
                    put(RatingMigration.ID_COLUMN, Order.ASCENDING);
                }})
                .rowMapper((resultSet, i) -> {
                    Comment comment = new Comment();
                    comment.setId(resultSet.getString(Comment.ID_COLUMN));
                    comment.setRating(resultSet.getString(Comment.Columns.RATING_COLUMN) == null ? null : resultSet.getInt(Comment.Columns.RATING_COLUMN));

                    //ugly perfomance after migration to mongo due to no join could be done. Additional migration SAL table can be done
                    Book book = repository.findOne(new BooleanBuilder().and(QBook.book.work.id.eq(resultSet.getString(Book.Columns.WORK_ID)))).orElse(null);
                    comment.setBook(book);

                    User user = new User();
                    user.setId(resultSet.getString(Comment.CREATED_BY_COLUMN));
                    comment.setCreatedBy(user);
                    return comment;
                })
                .saveState(false)
                .build()
                ;
    }

    @Bean
    public ItemProcessor<Comment, Comment> commentDoNothingProcessor() {
        return new PassThroughItemProcessor<>();
    }

    @Bean
    public MongoItemWriter<Comment> commentMongoItemWriter(MongoTemplate entityManagerFactory) {
        MongoItemWriter<Comment> writer = new MongoItemWriter<>();
        writer.setTemplate(entityManagerFactory);
        return writer;

    }

    @Bean
    public Step stepRatingMigrationLoadToCommentTable(MongoItemWriter<Comment> commentMongoItemWriter, TaskExecutor springBatchTaskExecutor, JdbcPagingItemReader<Comment> ratingMigrationToCommentReader, ItemProcessor<Comment, Comment> commentDoNothingProcessor) {
        return stepBuilderFactory.get("stepRatingMigrationLoadToCommentTable")
                .<Comment, Comment>chunk(5000)
                .reader(ratingMigrationToCommentReader)
                .processor(commentDoNothingProcessor)
                .writer(commentMongoItemWriter)
                .taskExecutor(springBatchTaskExecutor).throttleLimit(20)
                .build();
    }


    @Bean
    public Job importUserAuthorityAndCommentsFromRatingJob(Step stepInsertWorkWithoutBestBookIdFromBookMigration, Step stepInsertBookFromBookMigration, Step stepRatingCSVLoadToMigrationTable, Step stepRatingMigrationLoadToUserTable, Step stepRatingMigrationLoadToCommentTable) {
        return jobBuilderFactory.get("migrationFromKaggleDataSetJob")
                .incrementer(new RunIdIncrementer())
                .start(stepInsertWorkWithoutBestBookIdFromBookMigration)
                .next(stepInsertBookFromBookMigration)
                .next(stepRatingCSVLoadToMigrationTable)
                .next(stepRatingMigrationLoadToUserTable)
                .next(stepRatingMigrationLoadToCommentTable)
                .build();
    }

}