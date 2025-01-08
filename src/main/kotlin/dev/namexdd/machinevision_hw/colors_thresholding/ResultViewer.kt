/*
    Created by Sander Paju for TalTech
    Machine Vision. Lecturer: Daniil Valme

    About: Colors thresholding result viewer class
 */

package dev.namexdd.machinevision_hw.colors_thresholding

import dev.namexdd.machinevision_hw.MachineVisionApplication
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.nio.file.Files

object ResultViewer {

    fun show(result: AnalysisResult, fileName: String, minPercent: Double) {
        val stage = Stage()
        stage.title = "Analysis Results"

        // Сохранение обработанного изображения во временный файл
        val tempFile = File.createTempFile("processed", ".png")
        Imgcodecs.imwrite(tempFile.absolutePath, result.imageWithAnnotations)

        // Отображение изображения в ImageView
        val imageView = ImageView(Image(tempFile.toURI().toString()))
        imageView.fitWidth = 400.0
        imageView.isPreserveRatio = true

        // Отображение статистики
        val stats = buildString {
            appendLine("Result: $fileName")
            appendLine("Processing Time: ${result.processingTimeMs} ms")
            appendLine("Image Size: ${result.imageSize.width.toInt()}x${result.imageSize.height.toInt()}")
            appendLine("Min Percent: $minPercent%")
            appendLine("Detected Colors Objects:")
            result.colorCounts.forEach { (color, count) ->
                appendLine("  - $color: $count")
            }
        }
        val statsLabel = Label(stats)

        // Кнопка для сохранения результатов
        val saveButton = Button("Save Results")
        saveButton.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Save Results"
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
            val selectedFile = fileChooser.showSaveDialog(stage)
            if (selectedFile != null) {
                saveResultsToFile(selectedFile, stats, tempFile)
            }
        }

        // Создание интерфейса
        val layout = VBox(10.0, imageView, statsLabel, saveButton)
        layout.spacing = 10.0
        layout.prefWidth = 600.0
        stage.icons.add(Image(MachineVisionApplication::class.java.getResourceAsStream("/dev/namexdd/machinevision_hw/images/appicon.png")))
        stage.scene = Scene(layout, 600.0, 800.0)
        stage.setOnCloseRequest { Files.deleteIfExists(tempFile.toPath()) }
        stage.show()
    }

    private fun saveResultsToFile(file: File, stats: String, imageFile: File) {
        try {
            // Сохранение текстовой информации
            file.writeText(stats)

            // Сохранение обработанного изображения в том же каталоге
            val imageCopy = File(file.parentFile, "${file.nameWithoutExtension}_image.png")
            imageFile.copyTo(imageCopy, overwrite = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
