import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppointmentManager {
    private List<Appointment> appointments = new ArrayList<>();
    private int nextId = 1;

    public void addAppointment(Scanner scanner) {
        String patient;
        do {
            System.out.print("Enter patient name: ");
            patient = scanner.nextLine();
            if (!Validator.isValidName(patient)) {
                System.out.println("Invalid name. Try again.");
            }
        } while (!Validator.isValidName(patient));

        String doctor;
        do {
            System.out.print("Enter doctor name: ");
            doctor = scanner.nextLine();
            if (!Validator.isValidName(doctor)) {
                System.out.println("Invalid name. Try again.");
            }
        } while (!Validator.isValidName(doctor));

        String date;
        do {
            System.out.print("Enter date (YYYY-MM-DD): ");
            date = scanner.nextLine();
            if (!Validator.isValidDate(date)) {
                System.out.println("Invalid date format. Try again.");
            }
        } while (!Validator.isValidDate(date));

        String time;
        do {
            System.out.print("Enter time (HH:MM): ");
            time = scanner.nextLine();
            if (!Validator.isValidTime(time)) {
                System.out.println("Invalid time format. Try again.");
            }
        } while (!Validator.isValidTime(time));

        Appointment newApp = new Appointment(nextId++, patient, doctor, date, time, "Scheduled");
        appointments.add(newApp);
        System.out.println("Appointment added!");
        Logger.log("Added appointment for " + patient + " with Dr. " + doctor + " on " + date + " at " + time);
    }

    public void viewAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("List of Appointments:");
        for (Appointment app : appointments) {
            System.out.println("ID: " + app.getId() +
                    " | Patient: " + app.getPatientName() +
                    " | Doctor: " + app.getDoctorName() +
                    " | Date: " + app.getDate() +
                    " | Time: " + app.getTime() +
                    " | Status: " + app.getStatus());
        }
    }

    public void generateReport() {
        int total = appointments.size();
        long scheduled = appointments.stream()
                .filter(app -> app.getStatus().equalsIgnoreCase("Scheduled"))
                .count();
        long cancelled = appointments.stream()
                .filter(app -> app.getStatus().equalsIgnoreCase("Cancelled"))
                .count();

        System.out.println("\n--- Appointment Report ---");
        System.out.println("Total Appointments: " + total);
        System.out.println("Scheduled: " + scheduled);
        System.out.println("Cancelled: " + cancelled);
        Logger.log("Generated appointment report");
    }

    public void updateAppointment(Scanner scanner) {
        System.out.print("Enter appointment ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        for (Appointment app : appointments) {
            if (app.getId() == id) {
                System.out.print("Enter new status (Scheduled/Cancelled): ");
                String newStatus = scanner.nextLine();
                if (newStatus.equalsIgnoreCase("Scheduled") || newStatus.equalsIgnoreCase("Cancelled")) {
                    app.setStatus(newStatus);
                    System.out.println("Appointment status updated.");
                    Logger.log("Updated appointment ID " + id + " to status: " + newStatus);
                } else {
                    System.out.println("Invalid status. Must be 'Scheduled' or 'Cancelled'.");
                }
                return;
            }
        }
        System.out.println("Appointment not found.");
    }

    public void deleteAppointment(Scanner scanner) {
        System.out.print("Enter appointment ID to delete: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        boolean removed = appointments.removeIf(app -> app.getId() == id);
        if (removed) {
            System.out.println("Appointment deleted.");
            Logger.log("Deleted appointment ID " + id);
        } else {
            System.out.println("Appointment not found.");
        }
    }

    public void setAppointments(List<Appointment> loadedAppointments) {
        this.appointments = loadedAppointments;
        this.nextId = loadedAppointments.stream()
                .mapToInt(Appointment::getId)
                .max()
                .orElse(0) + 1;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
