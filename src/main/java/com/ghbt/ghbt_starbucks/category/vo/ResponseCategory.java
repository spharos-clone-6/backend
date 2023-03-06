package com.ghbt.ghbt_starbucks.category.vo;

import com.ghbt.ghbt_starbucks.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCategory {
    private Long id;
    private String name;
    private String type;

    public static List<ResponseCategory> mapper(List<Category> categories){
        List<ResponseCategory> responseCategories = new ArrayList<>();

        for (Category category : categories) {
            responseCategories.add(ResponseCategory.builder().id(category.getId()).name(category.getName()).type(category.getType()).build());
        }
        return responseCategories;
    }
}