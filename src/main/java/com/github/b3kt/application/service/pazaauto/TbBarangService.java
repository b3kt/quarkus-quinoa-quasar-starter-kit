package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.application.dto.PageRequest;
import com.github.b3kt.application.dto.PageResponse;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbBarangEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbBarangRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TbBarangService extends AbstractCrudService<TbBarangEntity, Long> {

    @Inject
    TbBarangRepository repository;

    @Override
    protected PanacheRepositoryBase<TbBarangEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbBarangEntity entity, Long id) {
        entity.setId(id);
    }

    @Override
    public PageResponse<TbBarangEntity> findPaginated(PageRequest pageRequest) {
        PanacheQuery<TbBarangEntity> query;

        // Apply search filter if specified
        if (pageRequest.getSearch() != null && !pageRequest.getSearch().isEmpty()) {
            String searchPattern = "%" + pageRequest.getSearch().toLowerCase() + "%";
            query = repository.find(
                    "lower(kodeBarang) like ?1 or lower(namaBarang) like ?1",
                    searchPattern);
        } else {
            query = repository.findAll();
        }

        // Apply sorting if specified
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            Sort sort = pageRequest.isDescending()
                    ? Sort.descending(pageRequest.getSortBy())
                    : Sort.ascending(pageRequest.getSortBy());
            query = query.page(Page.of(0, Integer.MAX_VALUE)); // Reset pagination for sorting

            // Re-apply query with sorting
            if (pageRequest.getSearch() != null && !pageRequest.getSearch().isEmpty()) {
                String searchPattern = "%" + pageRequest.getSearch().toLowerCase() + "%";
                query = repository.find(
                        "lower(kodeBarang) like ?1 or lower(namaBarang) like ?1",
                        sort,
                        searchPattern);
            } else {
                query = repository.findAll(sort);
            }
        }

        // Get total count
        long totalCount = query.count();

        // Apply pagination
        List<TbBarangEntity> rows = query.page(Page.of(pageRequest.getPage() - 1, pageRequest.getRowsPerPage())).list();

        return new PageResponse<>(rows, pageRequest.getPage(), pageRequest.getRowsPerPage(), totalCount);
    }
}
