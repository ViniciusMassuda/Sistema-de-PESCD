package br.ufscar.dc.dsw.sistema_pescd.domain;
public enum StatusAluno {
    NAO_ENVIADO("nao enviado"),
    PLANO_ENVIADO("plano enviado"),
    PLANO_APROVADO("plano aprovado"),
    DOCUMENTACAO_ENVIADA("documentacao enviada"),
    RELATORIO_ENVIADO("relatorio enviado"),
    RELATORIO_APROVADO_SUPERVISOR("relatorio aprovado pelo supervisor"),
    CONCLUIDO_RESPONSAVEL("concluido pelo responsavel");
    private final String descricao;
    StatusAluno(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
