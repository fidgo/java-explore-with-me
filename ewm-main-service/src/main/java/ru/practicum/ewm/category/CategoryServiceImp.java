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
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createByAdmin(NewCategoryDto newCategoryDto) {

        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new AlreadyExistException("This category already exist:" + newCategoryDto);
        }

        Category fromDto = CategoryMapper.toCategory(newCategoryDto);
        Category save = categoryRepository.save(fromDto);
        return CategoryMapper.toCategoryDto(save);
    }

    @Override
    @Transactional
    public CategoryDto updateByAdmin(CategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistException("This category already exist:" + categoryDto);
        }

        Category fromDto = CategoryMapper.toCategory(categoryDto);
        Category update = categoryRepository.save(fromDto);
        return CategoryMapper.toCategoryDto(update);
    }

    @Override
    @Transactional
    public List<CategoryDto> getListByPublic(PageRequestFrom pageRequest) {
        List<Category> categories = categoryRepository
                .findAll(pageRequest)
                .stream()
                .collect(Collectors.toList());

        return CategoryMapper.toCategoryDtos(categories);
    }

    @Override
    @Transactional
    public CategoryDto getByPublic(Long catId) {
        checkArgumentAndIfNullThrowException(catId, "catId");

        Category categoryFromId = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElemException("Category doesn't exist with id=" + catId));

        return CategoryMapper.toCategoryDto(categoryFromId);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long catId) {
        checkArgumentAndIfNullThrowException(catId, "catId");

        Category categoryFromId = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElemException("Category doesn't exist with id=" + catId));

        //TODO: проверка на наличии связу удаляемой категории с каким либо событием
        if (eventRepository.existsByCategory_Id(categoryFromId.getId())) {
            throw new AlreadyExistException("Unable to delete category! Category:"
                    + categoryFromId + "connect with event");
        }

        categoryRepository.deleteById(catId);
    }

    private void checkArgumentAndIfNullThrowException(Object variable, String title) {
        if (variable == null) {
            throw new IlLegalArgumentException(title + "is null");
        }
    }
}
