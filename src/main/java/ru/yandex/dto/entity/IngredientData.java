package ru.yandex.dto.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientData {
    @JsonProperty("_id")
    private String id;

    private String name;
    private String type;
    private Integer proteins;
    private Integer fat;
    private Integer carbohydrates;
    private Integer calories;
    private Integer price;
    private String image;

    @JsonProperty("image_mobile")
    private String imageMobile;

    @JsonProperty("image_large")
    private String imageLarge;

    @JsonProperty("__v")
    private Integer v;
}
