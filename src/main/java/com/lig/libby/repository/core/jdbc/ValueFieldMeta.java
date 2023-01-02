package com.lig.libby.repository.core.jdbc;

import com.lig.libby.domain.core.PersistentObject;
import com.querydsl.core.types.Path;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@Setter
@RequiredArgsConstructor
public class ValueFieldMeta<E extends PersistentObject, Q extends Path<E>, R> {

    @NonNull
    private final Path<R> fieldAlias;
    //can be null for @Formula fields
    private final String fieldColumn;
    @NonNull
    private final Function<E, R> fieldGetter;
    @NonNull
    private final BiConsumer<E, R> fieldSetter;
    @NonNull
    private final Class<R> fieldClass;

    public static String aliasQ(Path path) {
        return path.toString().replace(".", "_");
    }

    public String getFieldParentAlias() {
        return aliasQ(Objects.requireNonNull(fieldAlias.getMetadata().getParent()));
    }

    public String getFieldName() {
        return fieldAlias.getMetadata().getElement().toString();
    }

    public String getFieldAliasString() {
        return this.getFieldParentAlias() + "_" + this.getFieldName();
    }

    public List<String> getFieldSelectSQLFragment() {
        if (this.fieldAlias.getAnnotatedElement().isAnnotationPresent(Formula.class)) {
            return Arrays.asList(this.fieldColumn + " as " + this.getFieldAliasString());
        }
        return Arrays.asList(this.getFieldParentAlias() + "." + this.fieldColumn + " as " + this.getFieldAliasString());
    }
}
