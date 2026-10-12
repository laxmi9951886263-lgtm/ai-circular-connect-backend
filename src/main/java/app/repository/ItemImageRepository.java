
package app.repository;

import app.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemImageRepository
        extends JpaRepository<ItemImage, Long> {

    Optional<ItemImage> findByItemId(Long itemId);
}
