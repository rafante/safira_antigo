package com.br.asgardtecnologia.base

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook

/**
 * Groovy Builder that extracts data from
 * Microsoft Excel spreadsheets.
 * @author Goran Ehrsson
 */
class ExcelBuilder {

    static String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    def workbook
    def labels
    def row

    ExcelBuilder(InputStream inputStream) {
        HSSFRow.metaClass.getAt = { Integer idx ->
            def cell = delegate.getCell(idx)
            if (!cell) {
                return null
            }
            def value
            switch (cell.cellType) {
                case HSSFCell.CELL_TYPE_NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        value = cell.dateCellValue
                    } else {
                        value = cell.numericCellValue
                    }
                    break
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    value = cell.booleanCellValue
                    break
                case HSSFCell.CELL_TYPE_FORMULA:
                    value = cell.numericCellValue
                    break
                default:
                    value = cell.stringCellValue
                    break
            }
            return value
        }

        workbook = new HSSFWorkbook(inputStream)

    }

    def getSheet(idx) {
        def sheet
        if (!idx) idx = 0
        if (idx instanceof Number) {
            sheet = workbook.getSheetAt(idx)
        } else if (idx ==~ /^\d+$/) {
            sheet = workbook.getSheetAt(Integer.valueOf(idx))
        } else {
            sheet = workbook.getSheet(idx)
        }
        return sheet
    }

    def cell(Integer idx) {
        if (labels && (idx instanceof String)) {
            idx = labels.indexOf(idx.toLowerCase())
        }
        return row[idx]
    }

    Integer cellAsInteger(idx) {

        def cell = cell(idx)

        if (cell != null)
            return Utils.ToInteger(cell)
        else
            return 0

    }

    BigDecimal cellAsBigDecimal(idx) {
        def cell = cell(idx)

        if (cell != null)
            return Utils.ToBigDecimal(cell)
        else
            return 0
    }

    def propertyMissing(String name) {
        cell(name)
    }

    def eachLine(Map params = [:], Closure closure) {
        def offset = params.offset ?: 0
        def max = params.max ?: 9999999
        def sheet = getSheet(params.sheet)
        def rowIterator = sheet.rowIterator()
        def linesRead = 0

        if (params.labels) {
            labels = rowIterator.next().collect { it.toString().toLowerCase() }
        }
        offset.times { rowIterator.next() }

        closure.setDelegate(this)

        while (rowIterator.hasNext() && linesRead++ < max) {
            row = rowIterator.next()
            closure.call(row)
        }
    }

}