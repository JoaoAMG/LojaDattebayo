package com.joaoamg.dattebayo.dto;

import lombok.Data;
import java.util.UUID;


@Data
public class ItemPedidoInputDTO {
    private UUID produtoId;
    private int quantidade;
}
