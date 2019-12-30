/**
 * Created by diego on 16/07/14.
 */
class License {
    String serial

    // Variáveis estáticas
    static String activationCode
    static Date validade

    static mapping = { cache true }
    static constraints = {}

    /**
     *
     */
    public void setSerial(String value) {
        this.serial = value

        validateSerial(true)
    }

    /**
     * Valida o número de série e a validade
     * @return
     */
    public Boolean validateSerial() {
        return validateSerial(false)
    }

    /**
     * Valida o número de série e a validade
     * @return
     */
    public Boolean validateSerial(Boolean refreshStatic) {
        try {
            if (refreshStatic || activationCode == null || validade == null) {
                validade = null
                String dec = com.br.asgardtecnologia.license.License.decrypt(this.serial?.replace(" ", ""))

                int indexOfVL = dec.indexOf(com.br.asgardtecnologia.license.License.VL)
                activationCode = dec.substring(com.br.asgardtecnologia.license.License.AC.length(), indexOfVL)?.trim()
                validade = Date.parse("dd/MM/yyyy", dec.substring(indexOfVL +
                        com.br.asgardtecnologia.license.License.VL.length()))
            }

            Boolean isValid = (activationCode.equals(generateActivationCode()) && validade > new Date())
            return isValid
        } catch (Exception e) {
            return false
        }
    }

    /**
     * Gera um código de ativação
     *
     * @return
     */
    public static String generateActivationCode() {
        return com.br.asgardtecnologia.license.License.generateActivationCode();
    }

}
