public class Appointment {
    private int id;
    private String patientName;
    private String doctorName;
    private String date; // формат YYYY-MM-DD
    private String time; // формат HH:MM
    private String status; // Scheduled / Cancelled

    public Appointment(int id, String patientName, String doctorName, String date, String time, String status) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    // Преобразуем объект в строку для сохранения в файл
    public String toString() {
        return id + "," + patientName + "," + doctorName + "," + date + "," + time + "," + status;
    }

    // Создаем объект из строки из файла
    public static Appointment fromString(String line) {
        String[] parts = line.split(",");
        return new Appointment(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5]
        );
    }
}
