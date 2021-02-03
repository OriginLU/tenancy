package com.zeroone.tenancy.template;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> as of JPA 2.1, {@link EntityManagerFactory#unwrap} provides a nice approach as well,
 * in particular within configuration class arrangements:
 * <pre class="code">
 * &#064;Bean
 * public NativeQueryHibernateTemplate nativeQueryHibernateTemplate(@Qualifier("entityManagerFactory")EntityManagerFactory emf) {
 *      SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
 *      return new NativeQueryHibernateTemplate(sessionFactory);
 * }
 * </pre>
 * </p>
 */
public class NativeQueryHibernateTemplate extends HibernateTemplate {


    private final Map<Class<?>,String> sqlResultNameMap = new ConcurrentHashMap<>();

    private final Set<String> names = new HashSet<>();


    public NativeQueryHibernateTemplate(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    /**
     *
     * @param queryString  manual sql for query
     * @param resultClass it must be annotated by {@link SqlResultSetMapping}
     * @return return list result
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findByNativeQuery(String queryString,Class<T> resultClass){

        String resultSetName = getResultSetName(resultClass);

        return super.execute(session -> session
                .createSQLQuery(queryString)
                .setResultSetMapping(resultSetName)
                .list());
    }
    /**
     *
     * @param queryString  manual sql for query
     * @param resultClass it must be annotated by {@link SqlResultSetMapping}
     * @return return unique result
     */
    public <T> T findOneByNativeQuery(String queryString,Class<T> resultClass){

        String resultSetName = getResultSetName(resultClass);
        return resultClass.cast(super.execute(session -> session
                .createSQLQuery(queryString)
                .setResultSetMapping(resultSetName)
                .uniqueResult()));
    }


    private <T> String getResultSetName(Class<T> resultClass) {

        return sqlResultNameMap.computeIfAbsent(resultClass, k -> {

            SqlResultSetMapping sqlResultSetMapping = k.getDeclaredAnnotation(SqlResultSetMapping.class);
            if (sqlResultSetMapping == null){
                throw new IllegalStateException("not found sql result mapping annotation[@SqlResultSetMapping]");
            }

            EntityResult[] entities = sqlResultSetMapping.entities();
            if (entities.length == 0){
                throw new IllegalStateException("not found sql result mapping annotation[@EntityResult]");
            }

            String name = sqlResultSetMapping.name();
            if (!names.add(name)) {
                throw new IllegalStateException("sql result mapping name [" + name + "] is duplicate");
            }
            return name;
        });
    }




}
