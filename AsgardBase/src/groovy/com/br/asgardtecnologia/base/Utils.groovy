package com.br.asgardtecnologia.base

import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.export.JRXlsExporter
import net.sf.jasperreports.engine.export.JRXlsExporterParameter
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass

import java.text.DateFormat
import java.text.Normalizer
import java.text.SimpleDateFormat

class Utils {

    static String GetOnlyNumerics(String str) {

        if (str == null) {
            return null
        }

        StringBuffer strBuff = new StringBuffer()
        char c;

        for (Integer i = 0; i < str.length(); i++) {
            c = str.charAt(i)

            if (Character.isDigit(c)) {
                strBuff.append(c)
            }
        }
        return strBuff.toString()
    }

    static String GetOnlyNumericsAndLetters(String str) {

        if (str == null) {
            return null
        }

        StringBuffer strBuff = new StringBuffer()
        char c;

        for (Integer i = 0; i < str.length(); i++) {
            c = str.charAt(i)

            if (Character.isDigit(c) || Character.isLetter(c)) {
                strBuff.append(c)
            }
        }
        return strBuff.toString()
    }

    static String LeftZero(String number, Integer i) {
        return LeftZero(ToInteger(number), i)
    }

    static String LeftZero(Integer number, Integer i) {
        return String.format('%0' + i.toString() + 'd', number)
    }

    static String Substring(String str, Integer i) {
        if (str == null) return

        if (str.length() <= i)
            return str
        else
            return str.substring(0, i)
    }

    static ToAny(type, obj) {
        switch (type) {
            case Integer:
                return ToInteger(obj)
            case Long:
                return ToLong(obj)
            case Double:
                return ToDouble(obj)
            case BigDecimal:
                return ToBigDecimal(obj)
            case Date:
                return ToDate(obj)
            case Boolean:
                return ToBoolean(obj)

            default:
                return ToString(obj, "")
        }
    }

    static Integer ToInteger(Object obj) {
        ToInteger(obj, 0)
    }

    static Integer ToInteger(Object obj, Integer defaultValue) {
        try {
            return obj.toInteger()
        } catch (e) {
            return defaultValue
        }
    }

    static Long ToLong(Object obj) {
        ToLong(obj, 0)
    }

    static Long ToLong(Object obj, Integer defaultValue) {
        try {
            return obj.toLong()
        } catch (e) {
            return defaultValue
        }
    }

    static String ToString(Object obj, String defaultValue) {
        if (obj && obj.toString()) return obj.toString()
        else
            return defaultValue
    }

    static Double ToDouble(Object obj) {
        try {
            return obj.toDouble()
        } catch (e) {
            try {
                obj = obj?.toString().replace(".", "").replace(",", ".")

                return obj.toDouble()
            }
            catch (e1) {
                return 0
            }
        }
    }

    static BigDecimal ToBigDecimal(Object obj) {
        try {
            return obj.toBigDecimal()
        } catch (e) {
            try {
                obj = obj?.toString().replace(".", "").replace(",", ".")

                return obj.toBigDecimal()
            }
            catch (e1) {
                return 0
            }
        }
    }

    static Date ToDate(Object obj) {
        if (!obj) return new Date()
        if (obj == "") return null

        if (obj in Date) return obj

        try {
            return Date.parse('dd/MM/yyyy', obj.toString())
        }
        catch (e1) {
            try {
                return Date.parse('dd.MM.yy', obj.toString())
            }
            catch (e2) {
                try {
                    return Date.parse('dd.MM.yyyy', obj.toString())
                }
                catch (e3) {
                    try {
                        return Date.parse('yyyy-MM-dd', obj.toString())
                    }
                    catch (e4) {
                        try {
                            return Date.parse('yyyy-MM-dd', obj.toString())
                        } catch (e5) {
                            try {
                                obj = obj.toString()
                                        .replace("JAN", "01")
                                        .replace("FEV", "02")
                                        .replace("MAR", "03")
                                        .replace("ABR", "04")
                                        .replace("MAI", "05")
                                        .replace("JUN", "06")
                                        .replace("JUL", "07")
                                        .replace("AGO", "08")
                                        .replace("SET", "09")
                                        .replace("OUT", "10")
                                        .replace("NOV", "11")
                                        .replace("DEZ", "12")

                                return Date.parse('dd-MM-yyyy', obj.toString())
                            } catch (e6) {
                                DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                                Date date = (Date) formatter.parse(obj.toString());
                                return date
                            }
                        }
                    }
                }
            }
        }
    }

    static Boolean ToBoolean(obj) {
        return (obj && (obj != "false"))
    }

    static boolean ValidaCpf(String strCpf) {
        if (!strCpf.substring(0, 1).equals("")) {
            try {
                boolean validado = true;
                int d1, d2;
                int digito1, digito2, resto;
                int digitoCPF;
                String nDigResult;
                strCpf = strCpf.replace('.', ' ');
                strCpf = strCpf.replace('-', ' ');
                strCpf = strCpf.replaceAll(" ", "");
                d1 = d2 = 0;
                digito1 = digito2 = resto = 0;

                for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
                    digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount)).intValue();

                    //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
                    d1 = d1 + (11 - nCount) * digitoCPF;

                    //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
                    d2 = d2 + (12 - nCount) * digitoCPF;
                };

                //Primeiro resto da divisão por 11.  
                resto = (d1 % 11);

                //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
                if (resto < 2)
                    digito1 = 0;
                else
                    digito1 = 11 - resto;

                d2 += 2 * digito1;

                //Segundo resto da divisão por 11.  
                resto = (d2 % 11);

                //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.  
                if (resto < 2)
                    digito2 = 0;
                else
                    digito2 = 11 - resto;

                //Digito verificador do CPF que está sendo validado.  
                String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

                //Concatenando o primeiro resto com o segundo.  
                nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

                //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.  
                return nDigVerific.equals(nDigResult);
            } catch (Exception e) {
                System.err.println("Erro !" + e);
                return false;
            }
        } else return false;
    }

    static boolean ValidaCnpj(String str_cnpj) {
        if (!str_cnpj.substring(0, 1).equals("")) {
            try {
                str_cnpj = str_cnpj.replace('.', ' ');
                str_cnpj = str_cnpj.replace('/', ' ');
                str_cnpj = str_cnpj.replace('-', ' ');
                str_cnpj = str_cnpj.replaceAll(" ", "");
                int soma = 0, aux, dig;
                String cnpj_calc = str_cnpj.substring(0, 12);

                if (str_cnpj.length() != 14)
                    return false;
                char[] chr_cnpj = str_cnpj.toCharArray();
                /* Primeira parte */
                for (int i = 0; i < 4; i++)
                    if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                        soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                for (int i = 0; i < 8; i++)
                    if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
                        soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                dig = 11 - (soma % 11);
                cnpj_calc += (dig == 10 || dig == 11) ?
                        "0" : Integer.toString(dig);
                /* Segunda parte */
                soma = 0;
                for (int i = 0; i < 5; i++)
                    if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                        soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                for (int i = 0; i < 8; i++)
                    if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
                        soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                dig = 11 - (soma % 11);
                cnpj_calc += (dig == 10 || dig == 11) ?
                        "0" : Integer.toString(dig);
                return str_cnpj.equals(cnpj_calc);
            } catch (Exception e) {
                System.err.println("Erro !" + e);
                return false;
            }
        } else return false;

    }

    public static FormataValorSPED(valor) {
        return valor
    }

    public static ByteArrayOutputStream ExportReportPdf(JasperPrint jp) throws JRException, FileNotFoundException {
        JRPdfExporter exporter = new JRPdfExporter();

        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.exportReport();

        return fos
    }

    public static JasperReportOutput(response, JasperPrint jp, params) {
        def outputStream
        switch (params.extension.toUpperCase()) {
            case 'PDF':
                response.contentType = "application/pdf"
                outputStream = Utils.ExportReportPdf(jp).toByteArray()
                break

            case 'XLS':
                response.contentType = "application/xls"
                response.setHeader("Content-Disposition", "attachment;filename=\"" + params["action"] + ".xls" + "\"")
                outputStream = Utils.ExportReportXls(jp).toByteArray()
                break
        }

        response.outputStream << outputStream
        response.outputStream.flush()
        response.outputStream.close()

        return outputStream
    }

    public static ByteArrayOutputStream ExportReportXls(JasperPrint jp) throws JRException, FileNotFoundException {
        JRXlsExporter exporter = new JRXlsExporter();

        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE)
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE)
        exporter.exportReport();

        return fos
    }

    public static GetProperty(object, String property) {
        try {
            property?.tokenize('.').inject object, { obj, prop ->
                obj ? obj[prop] : null
            }
        } catch (e) {
            return null
        }
    }

    public static GrailsDomainClass GetDomainClass(GrailsApplication grailsApplication, domain) {
        domain = domain
        def domainClass = grailsApplication.getDomainClass(domain)

        return domainClass
    }

    public static String RemoveAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
    }

    public static Date GetInicioMes(mes, ano) {
        Calendar c = GregorianCalendar.instance
        c.set(ano, mes - 1, 1)
        return c.getTime()
    }

    public static String lat1ToUtf8(String lat1){
        return new String(lat1.getBytes("ISO-8859-1"), "UTF-8")
    }

    public static Date GetFimMes(mes, ano) {
        Calendar c = GregorianCalendar.instance
        c.set(ano, mes - 1, c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH))
        return c.getTime()
    }
}