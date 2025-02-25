package org.swiggy.orderservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swiggy.orderservice.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
