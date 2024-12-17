package ru.jetlabs.acquiringmockbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jetlabs.acquiringmockbackend.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Поиск пользователя по email
    Optional<UserEntity> findByEmail(String email);

    // Поиск пользователей по части имени
    List<UserEntity> findByNameContainingIgnoreCase(String namePart);

    // Проверка существования пользователя с email
    boolean existsByEmail(String email);

    // Поиск пользователей по email, заканчивающемуся на определенный домен
    @Query("SELECT u FROM UserEntity u WHERE u.email LIKE %:domain")
    List<UserEntity> findByEmailDomain(@Param("domain") String domain);

    // Удаление пользователя по email
    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.email = :email")
    void deleteByEmail(@Param("email") String email);

    // Обновление имени пользователя
    @Modifying
    @Query("UPDATE UserEntity u SET u.name = :newName WHERE u.id = :userId")
    void updateUserName(@Param("userId") Long userId, @Param("newName") String newName);

}
