Attribute VB_Name = "UpdateChart"
Private Function FindChart() As Scripting.dictionary
    Dim dictionary2 As Scripting.dictionary
    Dim shape As InlineShape
    Set dictionary2 = CreateObject("Scripting.dictionary")
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            shape.chart.ChartData.Activate
            dictionary2.Add shape.chart.ChartData.Workbook.Sheets(1).name, shape.chart
        End If
    Next shape
    Set FindChart = dictionary2
End Function

Private Sub UpdateALE(mychart)
    Dim chart As chart
    Dim workSheet As Excel.workSheet
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.ChartData.Workbook.Sheets(1)
    lastNotEmptyRow = workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row
    workSheet.Range("B2:B" & lastNotEmptyRow).NumberFormat = "### ### ### ###0k€"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range("A1:B" & lastNotEmptyRow).Address
    chart.PlotBy = xlColumns
    chart.ChartData.Workbook.Close
End Sub
Private Sub UpdateCompliance(mychart)
    Dim chart As chart
    Dim workSheet As Excel.workSheet
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.ChartData.Workbook.Sheets(1)
    If Not IsEmpty(workSheet.Cells(1, 1)) Then
        workSheet.Range(workSheet.Cells(2, 2), workSheet.Cells(workSheet.Range("A1").End(xlDown).Row, workSheet.Range("B1").End(xlToRight).Column)).NumberFormat = "0%"
        chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A1").End(xlDown).Row, workSheet.Range("B1").End(xlToRight).Column)).Address
        chart.Axes(xlValue).TickLabels.NumberFormat = "0%"
        chart.Axes(xlValue).MaximumScale = 1
        chart.Axes(xlValue).MinimumScale = 0
        chart.PlotBy = xlColumns
    End If
    chart.ChartData.Workbook.Close

End Sub
Private Sub UpdateBudget(mychart)
    Dim chart As chart
    Dim workSheet As Excel.workSheet
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.ChartData.Workbook.Sheets(1)
    workSheet.Range("B2:E" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyManDay
    workSheet.Range("F2:H" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "### ### ### ###0k€"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(xlDown).Row, workSheet.Range("B2").End(xlToRight).Column)).Address
    chart.PlotBy = xlColumns
    'Move all series to primary axis
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = xlPrimary
    Next serie
    
    chart.HasAxis(xlValue, xlSecondary) = True
    
    For i = 1 To 4
        chart.SeriesCollection(i).AxisGroup = xlSecondary
    Next
    
    'chart.Axes(xlValue, xlPrimary).TickLabels.NumberFormat = "### ### ### ###0k€"
    'chart.Axes(xlValue, xlSecondary).TickLabels.NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyManDay
    
    chart.ChartData.Workbook.Close
End Sub
Private Sub UpdateEvolutionOfProfitability(mychart)
    Dim chart As chart
    Dim workSheet As Excel.workSheet
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.ChartData.Workbook.Sheets(1)
    workSheet.Range("B2:C" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "0%"
    workSheet.Range("D2:D" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "### ### ### ###0k€"
    workSheet.Range("E2:G" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyKiloEuroPerYear
    workSheet.Range("H2:H" & workSheet.Range("A" & workSheet.Rows.count).End(xlUp).Row).NumberFormat = "### ### ### ###0"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(xlDown).Row, workSheet.Range("B2").End(xlToRight).Column)).Address
    chart.PlotBy = xlColumns
    
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = xlPrimary
    Next serie
    
    chart.HasAxis(xlCategory, xlSecondary) = True
    
    For i = 1 To 2
        chart.SeriesCollection(i).AxisGroup = xlSecondary
    Next
    
    chart.ChartData.Workbook.Close
End Sub

Sub UpdateGraphics()
Attribute UpdateGraphics.VB_Description = "Applique le style Normal et convertit les titres sélectionnés en corps de texte."
Attribute UpdateGraphics.VB_ProcData.VB_Invoke_Func = "TemplateProject.NewMacros.AbaisserEnCorpsDeTexte"
'
' AbaisserEnCorpsDeTexte Macro
' Applique le style Normal et convertit les titres sélectionnés en corps de texte.
'
    Dim charts As Scripting.dictionary
    Set charts = FindChart()
    For Each chartName In charts.Keys
        Select Case chartName
            Case "ALEByAsset", "ALEByAssetType", "ALEByScenario", "ALEByScenarioType"
                UpdateALE (charts(chartName))
             Case "Compliance27001", "Compliance27002"
                UpdateCompliance (charts(chartName))
            Case "Budget"
                UpdateBudget (charts(chartName))
            Case "EvolutionOfProfitability"
                UpdateEvolutionOfProfitability (charts(chartName))
        End Select
    Next chartName
End Sub
