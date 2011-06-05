package easyquery;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

public class EasyQueryProvider {
    
    private final EntityManager entityManager;

    public EasyQueryProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public <S> EasyQueryBuilder<S> select(Class<S> entityClass) {
        
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<S> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        
        Root<S> root = criteriaQuery.from(entityClass);
        
        criteriaQuery.select(root);
        
        return create(criteriaQuery, entityManager, criteriaBuilder, root);
    }
    
    public <E, S> EasyQueryBuilder<S> select(SingularAttribute<E, S> selectAttribute) {
        
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<S> criteriaQuery = criteriaBuilder.createQuery(selectAttribute.getJavaType());
        
        Root<E> root = criteriaQuery.from(selectAttribute.getDeclaringType().getJavaType());
        
        criteriaQuery.select(root.get(selectAttribute));
        
        return create(criteriaQuery, entityManager, criteriaBuilder, root);
    }

    private <E, S> EasyQueryBuilder<S> create(CriteriaQuery<S> criteriaQuery, EntityManager entityManager, CriteriaBuilder criteriaBuilder, Root<E> root) {
        
        return new EasyQueryBuilder<S>(
                criteriaQuery, 
                new QueryRunner(
                        entityManager,
                        new CriteriaQueryBuilder(
                                new WhereTransformer(criteriaBuilder, root),
                                new OrderByTransformer(criteriaBuilder, root)
                        )
                )
        );
    }
}
