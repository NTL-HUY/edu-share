package com.nbh.edushare.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
public class SoftDeleteModel extends BaseModel{
    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
