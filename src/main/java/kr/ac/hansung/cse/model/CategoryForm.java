package kr.ac.hansung.cse.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryForm {
    @NotBlank(message = "카테고리 이름을 입력하세요")  // 공백 불가
    @Size(max = 50, message = "카테고리 이름은 50자 이내로 입력하세요")  // 최대 길이를 50자로 제한
    private String name;
}