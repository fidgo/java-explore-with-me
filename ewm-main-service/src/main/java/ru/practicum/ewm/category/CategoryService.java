package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;

public interface CategoryService {
    CategoryDto createByAdmin(NewCategoryDto newCategoryDto);

    CategoryDto updateByAdmin(CategoryDto categoryDto);

    List<CategoryDto> getListByPublic(PageRequestFrom pageRequest);

    CategoryDto getByPublic(Long catId);

    void deleteByAdmin(Long catId);

}
