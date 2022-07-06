package com.app.dto;


import com.app.entiry.Dish;
import com.app.entiry.DishFlavor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DishDto extends Dish {


    private List<DishFlavor> flavors = new ArrayList<>();


    private String categoryName;


    private Integer copies;
}
