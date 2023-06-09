package org.flowershop.repository.repositoryTXT;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.flowershop.domain.tickets.Ticket;
import org.flowershop.repository.ITicketRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class TicketRepositoryTXT implements ITicketRepository {
    private static TicketRepositoryTXT instance;
    private Properties properties;
    private String fileTicket;
    private File file;

    private TicketRepositoryTXT() {
        properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileTicket = properties.getProperty("fileTicket");

        file = new File(fileTicket);
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // Create directory if not exists
            try {
                file.createNewFile(); // Create the file if not exists.
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static TicketRepositoryTXT getInstance() {
        if (instance == null) {
            instance = new TicketRepositoryTXT();
        }
        return instance;
    }
    

    public String getFileTicket() {
        return fileTicket;
    }

    @Override
    public Ticket addTicket(Ticket ticket) {

        try {
            String fileName = this.getFileTicket();
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file, true);
            writeLineInFile(fw, ticket);

        } catch (
                Exception e) {
            e.printStackTrace();

        }
        return ticket;

    }

    private void writeLineInFile(FileWriter fw, Ticket ticket) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();


        String jsonString = objectMapper.writeValueAsString(ticket);

        fw.write(jsonString);
        fw.write("\r\n");
        fw.close();
    }

    @Override
    public Ticket getTicketById(Long id) {
        ObjectMapper objectMapper = new ObjectMapper();
        Ticket ticket = null;

        File file;
        FileReader fr = null;
        BufferedReader br = null;
        String file1 = this.getFileTicket();

        Boolean encontrado = false;

        try {
            file = new File(file1);
            if (file.exists()) {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String linea;
                while ((linea = br.readLine()) != null && !encontrado) {
                    ticket = objectMapper.readValue(linea, Ticket.class);
                    if (ticket.getId() == id) encontrado = true;
                }
                if (encontrado) return ticket;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    @Override
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        File file;
        FileReader fr = null;
        BufferedReader br = null;
        String file1 = this.getFileTicket();

        try {
            file = new File(file1);
            if (file.exists()) {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String linea;
                Ticket ticket = null;
                while ((linea = br.readLine()) != null) {
                    ticket = objectMapper.readValue(linea, Ticket.class);
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tickets;

    }

    @Override
    public Long getNewTicketId() {
        Long lastId = 0L;

        ObjectMapper objectMapper = new ObjectMapper();

        File file;
        FileReader fr = null;
        BufferedReader br = null;
        String file1 = this.getFileTicket();

        try {
            file = new File(file1);
            if (file.exists()) {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String linea;
                Ticket ticket = null;
                while ((linea = br.readLine()) != null) {
                    ticket = objectMapper.readValue(linea, Ticket.class);
                    if (ticket.getId() > lastId) lastId = ticket.getId();
                }
                return lastId+1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastId;
    }

    @Override
    public Ticket removeTicketById(long id) throws IOException {
        List<Ticket> tickets = getAllTickets();

        Ticket ticket = null;

        List<Ticket> newTicketsList = tickets.stream()
                .filter(t -> t.getId() != id)
                .collect(Collectors.toList());

        String fileName = this.getFileTicket();
        ;
        File file = new File(fileName);
        FileWriter fw = new FileWriter(file, false);

        newTicketsList.forEach(t -> {
            try {
                writeLineInFile(fw, t);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return ticket;
    }

}

