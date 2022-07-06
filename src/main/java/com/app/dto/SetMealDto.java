package com.app.dto;

import com.app.entiry.SetMeal;
import com.app.entiry.SetMealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setMealDishes;

    private String categoryName;
}
