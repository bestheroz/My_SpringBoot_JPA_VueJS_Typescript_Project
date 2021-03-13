package com.github.bestheroz.demo.api.entity;

import com.github.bestheroz.standard.common.util.AuthenticationUtils;
import java.time.Instant;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractCreatedUpdateEntity {
  protected String createdBy;
  protected Instant created;
  protected String updatedBy;
  protected Instant updated;

  @PrePersist
  protected void onCreate() {
    this.updated = this.created = Instant.now();
    if (AuthenticationUtils.isLoggedIn()) {
      this.updatedBy = this.createdBy = AuthenticationUtils.getUserPk();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updated = Instant.now();
    if (AuthenticationUtils.isLoggedIn()) {
      this.updatedBy = AuthenticationUtils.getUserPk();
    }
  }
}
