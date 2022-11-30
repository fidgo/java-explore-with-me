package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.PageRequestFrom;
import ru.practicum.ewm.util.Update;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    CategoryDto createByAdmin(@Validated({Create.class}) @RequestBody NewCategoryDto newCategoryDto,
                              HttpServletRequest request) {

        log.info("{}:{}:{}#To create category with category={}",
                this.getClass().getSimpleName(),
                "createByAdmin",
                request.getRequestURI(),
                newCategoryDto
        );

        return categoryService.createByAdmin(newCategoryDto);
    }

    @PatchMapping("/admin/categories")
    CategoryDto updateByAdmin(@Validated({Update.class}) @RequestBody CategoryDto categoryDto,
                              HttpServletRequest request) {

        log.info("{}:{}:{}#To update category with category={}",
                this.getClass().getSimpleName(),
                "updateByAdmin",
                request.getRequestURI(),
                categoryDto
        );
        return categoryService.updateByAdmin(categoryDto);
    }

    @GetMapping("/categories")
    List<CategoryDto> getListByPublic(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size,
                                      HttpServletRequest request) {

        log.info("{}:{}:{}#To get categories from={} size={}",
                this.getClass().getSimpleName(),
                "getListByPublic",
                request.getRequestURI(),
                from,
                size
        );

        final PageRequestFrom pageRequest = new PageRequestFrom(size, from, Sort.unsorted());
        return categoryService.getListByPublic(pageRequest);
    }

    @GetMapping("/categories/{catId}")
    CategoryDto getByPublic(@PathVariable("catId") Long catId,
                            HttpServletRequest request) {

        log.info("{}:{}:{}#To get category with={}",
                this.getClass().getSimpleName(),
                "getByPublic",
                request.getRequestURI(),
                catId
        );

        return categoryService.getByPublic(catId);
    }

    @DeleteMapping("/admin/categories/{catId}")
    void deleteByAdmin(@PathVariable("catId") Long catId,
                       HttpServletRequest request) {

        log.info("{}:{}:{}#To delete category with={}",
                this.getClass().getSimpleName(),
                "deleteByAdmin",
                request.getRequestURI(),
                catId
        );

        categoryService.deleteByAdmin(catId);
    }

}
