package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repository.CategoryRepository;
import kr.ac.hansung.cse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // 클래스 기본값: 읽기 전용 트랜잭션
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return null;
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    // 전체 상품 목록 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID의 상품 상세 정보 조회
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 새 상품 등록
    @Transactional // readOnly = false (쓰기 가능)
    public Product createProduct(Product product) {
        // 비즈니스 검증(가격이 음수인 경우 예외 발생)
        if (product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        }
        return productRepository.save(product);
    }

    // 기존 상품 정보 업데이트
    @Transactional
    public Product updateProduct(Product product) {
        if (product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        }
        return productRepository.update(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    // 상품명 검색, 키워드 기반 검색 결과 반환
    public List<Product> searchByName(String keyword){
        return productRepository.findByNameContaining(keyword);
    }

    // 카테고리 검색, 특정 카테고리 ID에 해당하는 상품들만 조회
    public List<Product> searchByCategory(Long categoryId){
        return productRepository.findByCategoryId(categoryId);
    }
}
