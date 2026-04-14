package kr.ac.hansung.cse.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.ac.hansung.cse.model.Product;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//
@Repository
public class ProductRepository {

    private final EntityManagerFactoryInfo entityManagerFactoryInfo;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductRepository(EntityManagerFactoryInfo entityManagerFactoryInfo) {
        this.entityManagerFactoryInfo = entityManagerFactoryInfo;
    }

    // 모든 상품 목록 조회, JOIN FETCH로 연관된 카테고리 정보를 한 번의 Query로 가져옴(N+1 문제 방지)
    public List<Product> findAll() {
        TypedQuery<Product> query = entityManager
                .createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.category ORDER BY p.id ASC", Product.class);
        return query.getResultList();
    }

    // ID로 상품 상세 조회
    public Optional<Product> findById(Long id) {
        List<Product> result = entityManager
                .createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id", Product.class)
                .setParameter("id", id)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // 신규 상품 저장
    public Product save(Product product) {
        entityManager.persist(product);
        return product;
    }

    // 기존 상품 정보 업데이트
    public Product update(Product product) {
        return entityManager.merge(product);
    }

    // 상품 삭제
    public void delete(Long id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    // 상품명에 키워드가 포함된 상품 조회
    public List<Product> findByNameContaining(String keyword) {
        return entityManager.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.name LIKE :keyword", Product.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    // 특정 카테고리의 상품 조회
    public List<Product> findByCategoryId(Long categoryId) {
        return entityManager.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :cid", Product.class)
                .setParameter("cid", categoryId)
                .getResultList();
    }
}
