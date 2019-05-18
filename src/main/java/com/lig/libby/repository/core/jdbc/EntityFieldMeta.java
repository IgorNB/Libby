package com.lig.libby.repository.core.jdbc;

import com.lig.libby.domain.core.PersistentObject;
import com.querydsl.core.types.Path;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityFieldMeta<E extends PersistentObject, Q extends Path<E>, R extends PersistentObject, K extends Path<R>> extends ValueFieldMeta<E, Q, R> {
    @NonNull
    private final List<ValueFieldMeta<R, K, ?>> childMetha;
    @NonNull
    private final Supplier<R> fieldClassFactory;

    public EntityFieldMeta(@NonNull Path<R> fieldAlias, @NonNull String fieldColumn, @NonNull Function<E, R> fieldGetter, @NonNull BiConsumer<E, R> fieldSetter, @NonNull List<ValueFieldMeta<R, K, ?>> childMetha, @NonNull Class<R> fieldClass, @NonNull Supplier<R> fieldClassFactory) {
        super(fieldAlias, fieldColumn, fieldGetter, fieldSetter, fieldClass);
        this.childMetha = childMetha;
        this.fieldClassFactory = fieldClassFactory;
    }

    @Override
    public List<String> getFieldSelectSQLFragment() {
        return childMetha
                .stream()
                .map(cField ->
                        {
                            String childFieldParentAlias = this.getFieldParentAlias() + "_" + this.getFieldName();
                            if (cField.getFieldAlias().getAnnotatedElement().isAnnotationPresent(Formula.class)) {
                                return Arrays.asList(" null as " + childFieldParentAlias + "_" + cField.getFieldName());//formula in child entities is not supported
                            }
                            return Arrays.asList(childFieldParentAlias + "." + cField.getFieldColumn() + " as " + childFieldParentAlias + "_" + cField.getFieldName());
                        }
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


}
