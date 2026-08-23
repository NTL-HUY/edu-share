package com.nbh.edushare.modules.knowledge.repository;

import com.nbh.edushare.modules.knowledge.pojo.Category;
import com.nbh.edushare.modules.knowledge.pojo.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
