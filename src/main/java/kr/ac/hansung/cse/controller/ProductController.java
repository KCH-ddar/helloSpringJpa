package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.ProductNotFoundException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.model.ProductForm;
import kr.ac.hansung.cse.service.CategoryService;
import kr.ac.hansung.cse.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// 상품 관련 화면 이동 및 데이터 처리를 위한 Controller
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // Service 객체 의존성 주입
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 상품 목록 화면 조회
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model) {

        // 빈 검색창&전체 카테고리인 채로 검색 시 기본 주소로 리다이렉트
        if(keyword != null && keyword.isBlank() && categoryId == null) {
            return "redirect:/products";
        }

        List<Product> products = null;

        // 검색 조건에 따라 데이터 조회(검색어 우선)
        if(keyword != null && !keyword.isBlank()) {
            products = productService.searchByName(keyword);
        } else if(categoryId != null){
            products = productService.searchByCategory(categoryId);
        } else{
            products = productService.getAllProducts();
        }

        // 뷰로 데이터 전달
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);

        return "productList";
    }

    // 상품 상세 정보 화면 조회
    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        model.addAttribute("product", product);
        return "productView";
    }

    // 상품 등록 화면 표시
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "productForm";
    }

    // 상품 등록 화면에서 입력한 데이터 DB에 저장
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult, Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "productForm"; // 오류가 있는 채로 폼 뷰 재표시
        }

        Product product = productForm.toEntity();
        Category category = null;

        // 선택한 카테고리 ID가 있을 때만 DB에서 찾아옴
        if(productForm.getCategoryId() != null){
            category = categoryService.getCategoryById(productForm.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID 입니다.(" +  product.getCategory().getId() + ")"));
        }
        product.setCategory(category);
        Product savedProduct = productService.createProduct(product);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + savedProduct.getName() + "' 상품이 성공적으로 등록되었습니다.");

        return "redirect:/products";
    }

    // 상품 수정 화면 표시
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 엔티티 → DTO 변환 (기존 데이터로 폼 초기화)
        model.addAttribute("productForm", ProductForm.from(product));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "productEditForm";
    }

    // 상품 수정 화면에서 수정한 데이터 DB에 반영
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult, Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "productEditForm"; // 오류가 있는 채로 수정 폼 재표시
        }

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        Category category = null;

        // 수정한 카테고리 ID가 있을 때만 DB에서 찾아옴
        if(productForm.getCategoryId() != null){
            category = categoryService.getCategoryById(productForm.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID 입니다.(" +  product.getCategory().getId() + ")"));
        }

        // Form에서 넘어온 데이터로 기존 Entity 업데이트
        product.setName(productForm.getName());
        product.setCategory(category);
        product.setPrice(productForm.getPrice());
        product.setDescription(productForm.getDescription());

        productService.updateProduct(product);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + product.getName() + "' 상품 정보가 수정되었습니다.");
        return "redirect:/products/" + id;
    }

    // 상품 삭제
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        String productName = product.getName(); // 삭제 전 이름 저장
        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + productName + "' 상품이 삭제되었습니다.");
        return "redirect:/products";
    }
}
