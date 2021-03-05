package br.com.les.amstore.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class City extends DomainEntity{
    private String description;

    @OneToMany(mappedBy = "city", targetEntity = Address.class)
    private List<Address> addresses;

    @ManyToOne
    @JoinColumn(name="state_id")
    private State state;
}
