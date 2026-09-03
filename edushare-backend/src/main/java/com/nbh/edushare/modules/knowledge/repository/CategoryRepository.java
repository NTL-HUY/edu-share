package com.nbh.edushare.modules.knowledge.repository;

import com.nbh.edushare.modules.knowledge.pojo.Category;
import com.nbh.edushare.modules.knowledge.pojo.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {

}
