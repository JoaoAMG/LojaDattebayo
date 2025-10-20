package com.joaoamg.dattebayo.service;

import com.joaoamg.dattebayo.model.ItemPedido;
import com.joaoamg.dattebayo.model.Pedido;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmailConfirmacao(Pedido pedido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(pedido.getUsuario().getEmail());
            helper.setSubject("Confirmação do seu Pedido na Dattebayo Store!");

            String htmlBody = construirCorpoDoEmail(pedido);
            helper.setText(htmlBody, true); // true para interpretar como HTML

            mailSender.send(message);
            log.info("E-mail de confirmação enviado com sucesso para: {}", pedido.getUsuario().getEmail());

        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de confirmação para o pedido {}: {}", pedido.getId(), e.getMessage());
        }
    }

    private String construirCorpoDoEmail(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif;'>");
        sb.append("<h1>Olá, ").append(pedido.getUsuario().getNome()).append("!</h1>");
        sb.append("<p>O seu pedido na Dattebayo Store foi confirmado com sucesso. Obrigado pela sua compra!</p>");
        sb.append("<h3>Resumo do Pedido:</h3>");
        sb.append("<p><strong>ID do Pedido:</strong> ").append(pedido.getId()).append("</p>");
        sb.append("<p><strong>Data da Compra:</strong> ").append(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>");
        sb.append("<p><strong>Forma de Pagamento:</strong> ").append(pedido.getMeioPagamento() != null ? pedido.getMeioPagamento() : "Não especificado").append("</p>");
        sb.append("<hr>");

        // Tabela de itens
        sb.append("<table style='width: 100%; border-collapse: collapse;'>");
        sb.append("<thead><tr>");
        sb.append("<th style='text-align: left; padding: 8px; border-bottom: 1px solid #ddd;'>Item</th>");
        sb.append("<th style='text-align: center; padding: 8px; border-bottom: 1px solid #ddd;'>Quantidade</th>");
        sb.append("<th style='text-align: right; padding: 8px; border-bottom: 1px solid #ddd;'>Valor Unitário</th>");
        sb.append("<th style='text-align: right; padding: 8px; border-bottom: 1px solid #ddd;'>Subtotal</th>");
        sb.append("</tr></thead><tbody>");

        for (ItemPedido item : pedido.getItens()) {
            sb.append("<tr>");
            sb.append("<td style='padding: 8px; border-bottom: 1px solid #ddd;'>");
            sb.append("<img src='").append(item.getProduto().getImagemUrl()).append("' alt='").append(item.getProduto().getNome()).append("' width='50' style='vertical-align: middle; margin-right: 10px;'>");
            sb.append(item.getProduto().getNome());
            sb.append("</td>");
            sb.append("<td style='text-align: center; padding: 8px; border-bottom: 1px solid #ddd;'>").append(item.getQuantidade()).append("</td>");
            sb.append("<td style='text-align: right; padding: 8px; border-bottom: 1px solid #ddd;'>R$ ").append(item.getPrecoUnitario()).append("</td>");
            sb.append("<td style='text-align: right; padding: 8px; border-bottom: 1px solid #ddd;'>R$ ").append(item.getSubTotal()).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");
        sb.append("<hr>");
        sb.append("<h2 style='text-align: right;'>Valor Total: R$ ").append(pedido.getValorTotal()).append("</h2>");
        sb.append("</body></html>");

        return sb.toString();
    }
}