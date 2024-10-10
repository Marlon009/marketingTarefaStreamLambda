package br.edu.fatec.pg;

import br.edu.fatec.pg.Comment;


import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class EmailMarketing {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/comments";

    public static void main(String[] args) {
        try {
            List<Comment> comments = getCommentsFromAPI();
            System.out.println("Comentários obtidos: " + comments);

            // Capturar o e-mail e senha do usuário
            Scanner scanner = new Scanner(System.in);

            System.out.print("Digite seu e-mail: ");
            String username = scanner.nextLine();

            // Coletar a senha do usuário
            System.out.print("Digite sua senha: ");
            String password = scanner.nextLine();

            // Enviando e-mail para cada comentário
            for (Comment comment : comments) {
                String email = comment.getEmail();
                if (email == null) {
                    System.out.println("E-mail é nulo para o comentário ID: " + comment.getId());
                    continue; // Pula este comentário
                }

                if (!email.isEmpty()) {
                    System.out.println("Enviando e-mail para: " + email);
                    String messageBody = "Olá " + comment.getName() + ",\n\n" +
                            "Confira nossa nova campanha:\n" + comment.getBody();

                    sendEmail(username, password, email, messageBody);
                } else {
                    System.out.println("E-mail inválido para o comentário ID: " + comment.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Comment> getCommentsFromAPI() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        Gson gson = new Gson();
        return gson.fromJson(content.toString(), new TypeToken<List<Comment>>(){}.getType());
    }

    private static void sendEmail(String username, String password, String recipient, String messageBody) {
        System.out.println("Tentando enviar e-mail para: " + recipient);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Usando o próprio e-mail como remetente
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Nova Campanha!");
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("E-mail enviado para: " + recipient);
        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail para: " + recipient);
            e.printStackTrace();
        }
    }
}

// Classe auxiliar Comment



