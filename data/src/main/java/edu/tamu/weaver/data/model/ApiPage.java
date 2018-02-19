package edu.tamu.weaver.data.model;

import java.util.Iterator;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.response.ApiView;

public class ApiPage<M extends BaseEntity> implements Page<M> {

    private final Page<M> page;

    public ApiPage(Page<M> page) {
        this.page = page;
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public List<M> getContent() {
        return page.getContent();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public int getNumber() {
        return page.getNumber();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public int getNumberOfElements() {
        return page.getNumberOfElements();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public int getSize() {
        return page.getSize();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public Sort getSort() {
        return page.getSort();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    @Override
    public Pageable nextPageable() {
        return page.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return page.previousPageable();
    }

    @Override
    public Iterator<M> iterator() {
        return page.iterator();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @JsonView(ApiView.Partial.class)
    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public <S> Page<S> map(Converter<? super M, ? extends S> converter) {
        return page.map(converter);
    }

}
