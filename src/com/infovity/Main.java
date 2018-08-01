package com.infovity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

class MailThread implements Runnable{

    String email;
    String role;


    public MailThread(String email, String role) {
        this.email = email;
        this.role = role;
        Thread t = new Thread(this);
        t.start();
    }

    public void sendEmail(String body, String subject, String recipient) throws MessagingException{
        Map <String,String> envVars = System.getenv();
        String from = envVars.get("EMAIL");
        String smtpHost = "smtp.gmail.com";
        String port = "465";
        final String login = envVars.get("EMAIL");
        final String password = envVars.get("PASSWORD");
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.from", from);
        mailProps.put("mail.smtp.host", smtpHost);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", true);
        mailProps.put("mail.smtp.socketFactory.port", port);
        mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProps.put("mail.smtp.socketFactory.fallback", "false");
        mailProps.put("mail.smtp.starttls.enable", "true");

        Session mailSession = Session.getDefaultInstance(mailProps, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }

        });

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(from));
        String[] emails = { recipient };
        InternetAddress dests[] = new InternetAddress[emails.length];
        for (int i = 0; i < emails.length; i++) {
            dests[i] = new InternetAddress(emails[i].trim().toLowerCase());
        }
        message.setRecipients(Message.RecipientType.TO, dests);
        message.setSubject(subject, "UTF-8");
        Multipart mp = new MimeMultipart();
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setContent(body, "text/html;charset=utf-8");
        mp.addBodyPart(mbp);
        message.setContent(mp);
        message.setSentDate(new java.util.Date());

        Transport.send(message);
    }

    @Override
    public void run() {
        try {
            sendEmail("Player Role: " + this.role, "Wolf Game", this.email );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Mail Sent for :: " + this.email);
    }
}


class Wolf{
    public static final int KEY_PLAYERS = 3;
    public static String []titlesRepetitive = {"Villagers","Wolf"};
    public static String []titlesKeyPlayers = {"Doctor", "Policeman","Monitor"};

    public static void welcomeMessage(){
        System.out.println("--------Welcome-------\n Welcome to Wolf Player Selector\n");
    }
    public static String[] shuffleData(String []data){
        List <String> listing = new ArrayList<String>();
        for(String name : data){
            listing.add(name);
        }

        Random rr = new Random();

        int iter;
        while(true){
            iter = rr.nextInt(1000);
            if(iter != 0){
                break;
            }
        }

        for(int counter=0;counter<iter;counter++)
        Collections.shuffle(listing);


        for(int i=0;i<listing.size();i++){
            data[i] = listing.get(i);
        }
        return data;
    }

    public static String[] reducedPlayersExpulsion(int numberRepPlayers){
        String []playerRoles = new String[numberRepPlayers];

        int limiter = numberRepPlayers/2+1;
        for(int i=0;i<limiter;i++)
            playerRoles[i] = titlesRepetitive[0];
        for(int i=limiter;i<playerRoles.length;i++)
            playerRoles[i] = titlesRepetitive[1];


        return playerRoles;
    }

    public static HashMap<String, String> sendPlayerTitle(int playersSize) {
        Scanner in = new Scanner(System.in);
        String []players = new String[playersSize];
        System.out.println("\n For the Better experience of the game please enter Entire Email of the players");
        for(int counter=0;counter<players.length;counter++){
            players[counter] = in.nextLine();
        }
        HashMap <String,String> assignedData = new HashMap<String, String>();

        List playerConstruct  = new ArrayList(Arrays.asList(reducedPlayersExpulsion(playersSize-KEY_PLAYERS)));

        playerConstruct.addAll(Arrays.asList(titlesKeyPlayers));

        Object []playerConstructArray = playerConstruct.toArray();

        String []playerRoles = new String[playersSize];

        for(int i=0;i<playerConstructArray.length;i++){
            playerRoles[i] = (String) playerConstructArray[i];
        }



        players = shuffleData(players);
        playerRoles = shuffleData(playerRoles);

        for(int i=0;i<players.length;i++){
            assignedData.put(players[i],playerRoles[i]);
        }
        return assignedData;
    }
}

public class Main{
    public void App(){
        Scanner in = new Scanner(System.in);
        Wolf w = new Wolf();
        w.welcomeMessage();
        int playersSize = in.nextInt();
        Map <String, String>playersAssignedData = w.sendPlayerTitle(playersSize);
        for(Map.Entry<String,String> entrySetterLoop : playersAssignedData.entrySet() ){
            //w.sendTestEmail("Player Role: " + entrySetterLoop.getValue(), "Wolf Game No.3", entrySetterLoop.getKey() );
            new MailThread(entrySetterLoop.getKey(),entrySetterLoop.getValue());
        }
    }
    public static void main(String[] args) {
        Main m = new Main();
        m.App();
    }
}