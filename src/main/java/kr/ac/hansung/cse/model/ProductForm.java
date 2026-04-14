package kr.ac.hansung.cse.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// 웹 화면과 데이터를 주고받기 위한 클래스
@Getter
@Setter
@NoArgsConstructor // Spring MVC 폼 바인딩 필수: 기본 생성자가 있어야 합니다.
public class ProductForm {
    // 상품 ID
    private Long id;

    // 상품명(필수 입력)
    @NotBlank(message = "상품명은 필수 입력 항목입니다.")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;

    // 카테고리 ID(선택 입력, 선택하지 않으면 null)
    private Long categoryId;

    // 가격(필수 입력)
    @NotNull(message = "가격은 필수 입력 항목입니다.")
    @DecimalMin(value = "0", inclusive = true, message = "가격은 0원 이상이어야 합니다.")
    @Digits(integer = 8, fraction = 2, message = "가격은 최대 99,999,999원까지, 소수점 2자리 이하로 입력해 주세요.")
    private BigDecimal price;

    // 상품 설명(선택 입력)
    @Size(max = 1000, message = "상품 설명은 1,000자 이하여야 합니다.")
    private String description;

    // 입력받은 데이터를 DB에 저장할 Entity로 변환
    public Product toEntity() {
        return new Product(this.name, null, this.price, this.description);
    }

    // 기존 Entity 데이터를 화면에 보여주기 위해 DTO 변환
    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.id = product.getId();
        form.name = product.getName();
        form.categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        form.price = product.getPrice();
        form.description = product.getDescription();
        return form;
    }
}
