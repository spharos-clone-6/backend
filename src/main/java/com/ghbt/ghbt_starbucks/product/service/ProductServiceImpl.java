package com.ghbt.ghbt_starbucks.product.service;

import com.ghbt.ghbt_starbucks.category.model.Category;
import com.ghbt.ghbt_starbucks.category.repository.ICategoryRepository;
import com.ghbt.ghbt_starbucks.category.vo.ResponseCategory;
import com.ghbt.ghbt_starbucks.error.ServiceException;
import com.ghbt.ghbt_starbucks.product.Projection.IProductSearch;
import com.ghbt.ghbt_starbucks.product.model.Product;
import com.ghbt.ghbt_starbucks.product.Projection.IProductListByCategory;
import com.ghbt.ghbt_starbucks.product.repository.IProductRepository;
import com.ghbt.ghbt_starbucks.product.vo.RequestProduct;
import com.ghbt.ghbt_starbucks.product.vo.ResponseProduct;
import com.ghbt.ghbt_starbucks.product_and_category.model.ProductAndCategory;
import com.ghbt.ghbt_starbucks.product_and_category.repository.IProductAndCategoryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
    @Autowired
    private final IProductRepository iProductRepository;
    private final IProductAndCategoryRepository iProductAndCategoryRepository;
    private final ICategoryRepository iCategoryRepository;


    @Override
    public ResponseProduct addProduct(RequestProduct requestProduct){
        Product product = Product.builder()
                .name(requestProduct.getName())
                .description(requestProduct.getDescription())
                .price(requestProduct.getPrice())
                .stock(requestProduct.getStock())
                .build();
        Product savedProduct = iProductRepository.save(product);

        for (String name :requestProduct.getCategoryList()) {
            Category findCategory = iCategoryRepository.findByName(name);
            ProductAndCategory productAndCategory = ProductAndCategory.builder()
                    .product(savedProduct)
                    .category(findCategory)
                    .build();
            ProductAndCategory resProductAndCategory = iProductAndCategoryRepository.save(productAndCategory);
        }

        ResponseProduct responseProduct = ResponseProduct.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .description(savedProduct.getDescription())
                .stock(savedProduct.getStock())
                .build();

        return responseProduct;
    }
    @Override
    public ResponseProduct getProduct(Long id) {
        Product product =iProductRepository.findById(id).orElseThrow(()-> new ServiceException("찾으려는 ID의 상품이 없습니다", HttpStatus.NO_CONTENT));
        ResponseProduct responseProduct = ResponseProduct.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .stock(product.getStock())
                .build();
        return responseProduct;
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> productList = iProductRepository.findAll();
        if (productList.isEmpty()) {
            throw new ServiceException("상품이 없습니다.",HttpStatus.NO_CONTENT);
        }
        return productList;
    }

    @Override
    public List<IProductListByCategory> getProductForCategory(String search) {
        List<IProductListByCategory> productList = iProductRepository.findAllProductType(search);
        if (productList.isEmpty()) {
            throw new ServiceException("검색 결과가 없습니다.",HttpStatus.NO_CONTENT);
        }
        return productList;
    }

    @Override
    public List<IProductSearch> getSearchProduct(String search) {
        List<IProductSearch> productList = iProductRepository.findProduct(search);
        if (productList.isEmpty()) {
            throw new ServiceException("검색 결과가 없습니다.", HttpStatus.NO_CONTENT);
        }
        return productList;
    }
    @Override
    public Page<Product> getList(Pageable pageable){
        Page<Product> paging = iProductRepository.findAll(pageable);
        return paging;

    }
}
