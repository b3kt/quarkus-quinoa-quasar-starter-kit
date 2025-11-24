package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.application.dto.PageRequest;
import com.github.b3kt.application.dto.PageResponse;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSpkEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbSpkRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TbSpkService extends AbstractCrudService<TbSpkEntity, Long> {

    @Inject
    TbSpkRepository repository;

    @Override
    protected PanacheRepositoryBase<TbSpkEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbSpkEntity entity, Long id) {
        entity.setId(id);
    }

    @Override
    public PageResponse<TbSpkEntity> findPaginated(PageRequest pageRequest) {
        PanacheQuery<TbSpkEntity> query;

        // Build query with filters
        String queryString = "1=1";
        Object[] params = new Object[0];
        int paramIndex = 1;

        // Apply search filter if specified
        if (pageRequest.getSearch() != null && !pageRequest.getSearch().isEmpty()) {
            String searchPattern = "%" + pageRequest.getSearch().toLowerCase() + "%";
            queryString += " and (lower(noSpk) like ?" + paramIndex + " or lower(nopol) like ?" + paramIndex
                    + " or lower(namaKaryawan) like ?" + paramIndex + ")";
            params = new Object[] { searchPattern };
            paramIndex++;
        }

        // Apply status filter if specified
        if (pageRequest.getStatusFilter() != null && !pageRequest.getStatusFilter().isEmpty()) {
            String[] statuses = pageRequest.getStatusFilter().split(",");
            StringBuilder statusQuery = new StringBuilder(" and (");
            Object[] newParams = new Object[params.length + statuses.length];
            System.arraycopy(params, 0, newParams, 0, params.length);

            for (int i = 0; i < statuses.length; i++) {
                if (i > 0) {
                    statusQuery.append(" or ");
                }
                statusQuery.append("statusSpk = ?").append(paramIndex);
                newParams[params.length + i] = statuses[i].trim().toUpperCase();
                paramIndex++;
            }
            statusQuery.append(")");
            queryString += statusQuery.toString();
            params = newParams;
        }

        // Create query
        if (params.length > 0) {
            query = repository.find(queryString, params);
        } else {
            query = repository.find(queryString);
        }

        // Apply sorting if specified
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            Sort sort = pageRequest.isDescending()
                    ? Sort.descending(pageRequest.getSortBy())
                    : Sort.ascending(pageRequest.getSortBy());

            // Re-apply query with sorting
            if (params.length > 0) {
                query = repository.find(queryString, sort, params);
            } else {
                query = repository.find(queryString, sort);
            }
        }

        // Get total count
        long totalCount = query.count();

        // Apply pagination
        List<TbSpkEntity> rows = query.page(Page.of(pageRequest.getPage() - 1, pageRequest.getRowsPerPage())).list();

        return new PageResponse<>(rows, pageRequest.getPage(), pageRequest.getRowsPerPage(), totalCount);
    }

    public String getNextSpkNumber(String spkNumber) {
        final String spkPattern = spkNumber + "%";
        return repository
                .find("where noSpk LIKE :spkPattern order by id desc", Parameters.with("spkPattern", spkPattern))
                .list()
                .stream()
                .map(TbSpkEntity::getNoSpk)
                .findFirst()
                .orElse(spkNumber + "00");
    }
}
