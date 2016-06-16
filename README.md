# VotaFCT
Sistema de votação para Android para criado no minicurso de Aplicativos Android Web na UNESP-FCT ministrado de 13/06/2016 à 17/06/2016. O diagrama de Sequência do arquivo VotaFCTDiagSeq.asta reflete o atual modelo de troca de mensagens do sistema. 

Ferramentas utilizadas:
- Android Studio
- Eclipse
- Apache Tomcat 7
- MySQL Server
- MySQL Workbench

---------------------
|Instruções para uso|
---------------------

1. Banco de dados: O arquivo VotaFCT.mwb contém o modelo do banco de dados MySQL Server. Para criar efetivamente o banco pode ser utilizado o MySQL Workbench, bastando para isso abrir o arquivo e usar opção Forward Engineer na aba Database. Os campos idUser e password devem estar populados para refletir as entradas dos usuários no aplicativo Android.

2. Aplicação Servidor VotaFCTServer: A classe Conexao.java, dentro do pacote database, deve ter seu login e senha preenchidos de acordo com a conta de usuário criada no MySQL Server o login e senha para acesso ao banco de dados.

3. Aplicação Android VotaFCT: A variável IPAddress da classe IPAddres .java, dentro do pacote services, deve ser preenchida com o endereço IP que estiver rodando o servidor VotaFCTServer 

