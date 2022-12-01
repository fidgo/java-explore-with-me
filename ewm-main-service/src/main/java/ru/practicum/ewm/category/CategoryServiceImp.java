package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.error.AlreadyExistException;
import ru.practicum.ewm.error.IlLegalArgumentException;
import ru.practicum.ewm.error.NoSuchElemException;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.util.PageRequestFrom;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createByAdmin(NewCategoryDto newCategoryDto) {

        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new AlreadyExistException("This category already exist:" + newCategoryDto);
        }

        Category inputCategory = CategoryMapper.toCategory(newCategoryDto);
        Category save = categoryRepository.save(inputCategory);
        return CategoryMapper.toCategoryDto(save);
    }

    @Override
    @Transactional
    public CategoryDto updateByAdmin(CategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistException("This category already exist:" + categoryDto);
        }

        Category inputCategory = CategoryMapper.toCategory(categoryDto);
        Category update = categoryRepository.save(inputCategory);
        return CategoryMapper.toCategoryDto(update);
    }

    @Override
    public List<CategoryDto> getListByPublic(PageRequestFrom pageRequest) {
        List<Category> categories = categoryRepository
                .findAll(pageRequest)
                .stream()
                .collect(Collectors.toList());

        return CategoryMapper.toCategoryDtos(categories);
    }

    @Override
    public CategoryDto getByPublic(Long catId) {
        checkArgumentAndIfNullThrowException(catId, "catId");

        Category categoryById = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElemException("Category doesn't exist with id=" + catId));

        return CategoryMapper.toCategoryDto(categoryById);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long catId) {
        checkArgumentAndIfNullThrowException(catId, "catId");

        Category categoryById = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElemException("Category doesn't exist with id=" + catId));

        if (eventRepository.existsByCategory_Id(categoryById.getId())) {
            throw new AlreadyExistException("Unable to delete category! Category:"
                    + categoryById + "connect with event");
        }

        categoryRepository.deleteById(catId);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
