import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String FILE_NAME = "appointments.txt";

    // Сохраняем список в обычный текстовый файл
    public static void saveAppointments(List<Appointment> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                writer.write(app.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }

    // Загружаем список из обычного файла
    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Appointment app = Appointment.fromString(line);
                appointments.add(app);
            }
        } catch (FileNotFoundException e) {
            // OK если файл не найден при первом запуске
        } catch (IOException e) {
            System.out.println("Error reading appointments: " + e.getMessage());
        }

        return appointments;
    }

    // ✅ Экспорт в CSV
    public static void exportToCSV(List<Appointment> appointments, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID,Patient,Doctor,Date,Time,Status\n");
            for (Appointment app : appointments) {
                writer.write(app.getId() + "," +
                        app.getPatientName() + "," +
                        app.getDoctorName() + "," +
                        app.getDate() + "," +
                        app.getTime() + "," +
                        app.getStatus() + "\n");
            }
            System.out.println("Appointments exported to " + filePath);
        } catch (IOException e) {
            System.out.println("Error exporting to CSV: " + e.getMessage());
        }
    }

    // ✅ Импорт из CSV
    public static List<Appointment> importFromCSV(String filePath) {
        List<Appointment> appointments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Пропускаем заголовок
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Appointment app = new Appointment(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5]
                    );
                    appointments.add(app);
                }
            }
            System.out.println("Appointments imported from " + filePath);
        } catch (IOException e) {
            System.out.println("Error importing from CSV: " + e.getMessage());
        }

        return appointments;
    }
}
