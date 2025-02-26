package io.hyperfoil.tools.horreum.entity.json;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@RegisterForReflection
@Table(
   name = "hook",
   uniqueConstraints = {
      @UniqueConstraint(
         columnNames = {"url","type","target"}
      )
   }
)
public class Hook extends PanacheEntityBase {
   @JsonProperty(required = true)
   @Id
   @SequenceGenerator(
      name = "hookSequence",
      sequenceName = "hook_id_seq",
      allocationSize = 1,
      initialValue = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hookSequence")
   public Integer id;

   @NotNull
   @Column(name = "url")
   public String url;

   @NotNull
   @Column(name = "type")
   public String type;

   @NotNull
   @Column(name = "target")
   public Integer target;

   @NotNull
   public boolean active;

}
