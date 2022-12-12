package gb.back.lesson5.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Product {

    private Long id;
    private Integer price;
    private String title;
    private String categoryTitle;

}
