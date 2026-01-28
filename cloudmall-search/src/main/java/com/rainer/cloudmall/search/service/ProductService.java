package com.rainer.cloudmall.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.rainer.cloudmall.common.to.es.SkuEsModel;
import com.rainer.cloudmall.search.constant.ElasticSearchConstants;
import com.rainer.cloudmall.common.exception.ProductPublishException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ElasticsearchClient elasticsearchClient;

    public ProductService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void publishProduct(List<SkuEsModel> skuEsModels) throws IOException {
        if (CollectionUtils.isEmpty(skuEsModels)) {
            return;
        }

        BulkRequest.Builder builder = new BulkRequest.Builder();
        for (SkuEsModel skuEsModel : skuEsModels) {
            builder.operations(op -> op
                    .index(idx -> idx
                            .index(ElasticSearchConstants.PRODUCT_INDEX)
                            .id(skuEsModel.getSkuId().toString())
                            .document(skuEsModel)
                    )
            );
        }
        BulkResponse result = elasticsearchClient.bulk(builder.build());
        if (result.errors()) {
            log.error("商品上架异常");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
            throw new ProductPublishException();
        }
    }
}
