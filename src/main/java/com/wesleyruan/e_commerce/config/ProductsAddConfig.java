package com.wesleyruan.e_commerce.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.wesleyruan.e_commerce.domain.model.ProductModel;
import com.wesleyruan.e_commerce.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Configuration
public class ProductsAddConfig implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Evita duplicar produtos toda vez que subir a aplicação
        if (productRepository.count() > 0) {
            System.out.println("Products already exist.");
            return;
        }

        List<ProductModel> products = List.of(
            new ProductModel(null, "Notebook Dell", "Notebook i7 16GB RAM", 4500.0, 10, true, null, null),
            new ProductModel(null, "Mouse Gamer", "Mouse RGB 7200 DPI", 150.0, 50, true, null, null),
            new ProductModel(null, "Teclado Mecânico", "Switch Blue ABNT2", 300.0, 30, true, null, null),
            new ProductModel(null, "Monitor 24\"", "Full HD IPS", 900.0, 20, true, null, null),
            new ProductModel(null, "Headset Gamer", "Som Surround 7.1", 250.0, 40, true, null, null),
            new ProductModel(null, "Cadeira Gamer", "Ergonômica reclinável", 1200.0, 15, true, null, null),
            new ProductModel(null, "SSD 1TB", "NVMe alta velocidade", 500.0, 25, true, null, null),
            new ProductModel(null, "HD Externo 2TB", "USB 3.0", 400.0, 18, true, null, null),
            new ProductModel(null, "Webcam Full HD", "1080p com microfone", 200.0, 35, true, null, null),
            new ProductModel(null, "Microfone Condensador", "Estúdio profissional", 350.0, 12, true, null, null)
        );

        productRepository.saveAll(products);

        System.out.println("Products created successfully.");
    }
}