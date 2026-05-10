package com.marija.middleearthbattle.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.Optional;

public final class ImageService {

    private ImageService() {
    }

    public static Optional<Image> loadImage(String imagePath) {
        URL resource = ImageService.class.getResource("/com/marija/middleearthbattle/images/" + imagePath);

        if (resource == null) {
            return Optional.empty();
        }

        Image image = new Image(resource.toExternalForm(), true);

        if (image.isError()) {
            return Optional.empty();
        }

        return Optional.of(image);
    }

    public static void setImage(ImageView imageView, String imagePath) {
        loadImage(imagePath).ifPresentOrElse(image -> {
            imageView.setImage(image);
            imageView.setVisible(true);
            imageView.setManaged(true);
        }, () -> {
            imageView.setImage(null);
            imageView.setVisible(false);
            imageView.setManaged(false);
        });
    }

    public static void setBackgroundImageIfExists(Region region, String imagePath) {
        URL resource = ImageService.class.getResource("/com/marija/middleearthbattle/images/" + imagePath);

        if (resource == null) {
            return;
        }

        region.setStyle(
                "-fx-background-image: url('" + resource.toExternalForm() + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }
}