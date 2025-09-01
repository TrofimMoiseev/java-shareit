package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBookerId(Long userId, Sort sort);

    Collection<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long userId,
            LocalDateTime now,
            LocalDateTime now1,
            Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByItem_OwnerId(Long ownerId, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime now, LocalDateTime now1, Sort sort);

    List<Booking> findByItem_OwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_OwnerIdAndStatus(Long ownerId, Status status, Sort sort);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.start < :now " +
            "AND b.status <> 'REJECTED'")
    boolean existsPastBookingExcludingRejected(@Param("userId") Long userId,
                                               @Param("itemId") Long itemId,
                                               @Param("now") LocalDateTime now);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, Status status);
}
