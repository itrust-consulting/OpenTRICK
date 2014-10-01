Attribute VB_Name = "UpdateChart"
Dim myxlUp As Variant
Dim myXlDown As Variant
Dim myXlColumns As Variant
Dim myXlValue As Variant
Dim myXlPrimary As Variant
Dim myXlSecondary As Variant
Dim myXlToRight As Variant
Private Sub Initialisation()
    myxlUp = -4162
    myXlDown = -4121
    myXlColumns = 2
    myXlValue = 2
    myXlPrimary = 1
    myXlSecondary = 2
    myXlToRight = -4161
End Sub

Private Function FindChart() As Variant
    Dim dictionary2 As Variant
    Dim shape As InlineShape
    Set dictionary2 = CreateObject("Scripting.dictionary")
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            shape.chart.chartData.Activate
            dictionary2.Add shape.chart.chartData.Workbook.Sheets(1).name, shape.chart
        End If
    Next shape
    Set FindChart = dictionary2
End Function

Private Sub UpdateALE(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    lastNotEmptyRow = workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row
    workSheet.Range("B2:B" & lastNotEmptyRow).NumberFormat = "### ### ### ###0k€"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range("A1:B" & lastNotEmptyRow).Address
    chart.PlotBy = myXlColumns
    chart.chartData.Workbook.Close
End Sub
Private Sub UpdateCompliance(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    If Not IsEmpty(workSheet.Cells(1, 1)) Then
        workSheet.Range(workSheet.Cells(2, 2), workSheet.Cells(workSheet.Range("A1").End(myXlDown).Row, workSheet.Range("B1").End(myXlToRight).Column)).NumberFormat = "0%"
        chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A1").End(myXlDown).Row, workSheet.Range("B1").End(myXlToRight).Column)).Address
        chart.Axes(xlValue).TickLabels.NumberFormat = "0%"
        chart.Axes(xlValue).MaximumScale = 1
        chart.Axes(xlValue).MinimumScale = 0
        chart.PlotBy = myXlColumns
    End If
    chart.chartData.Workbook.Close

End Sub
Private Sub UpdateBudget(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    workSheet.Range("B2:E" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyManDay
    workSheet.Range("F2:H" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "### ### ### ###0k€"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(myXlDown).Row, workSheet.Range("B2").End(myXlToRight).Column)).Address
    chart.PlotBy = myXlColumns
    'Move all series to primary axis
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = xlPrimary
    Next serie
    
    chart.HasAxis(myXlValue, myXlSecondary) = True
    
    For i = 1 To 4
        chart.SeriesCollection(i).AxisGroup = myXlSecondary
    Next
    
    'chart.Axes(xlValue, xlPrimary).TickLabels.NumberFormat = "### ### ### ###0k€"
    'chart.Axes(xlValue, xlSecondary).TickLabels.NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyManDay
    
    chart.chartData.Workbook.Close
End Sub
Private Sub UpdateEvolutionOfProfitability(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    workSheet.Range("B2:C" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "0%"
    workSheet.Range("D2:D" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "### ### ### ###0k€"
    workSheet.Range("E2:G" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyKiloEuroPerYear
    workSheet.Range("H2:H" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).Row).NumberFormat = "### ### ### ###0"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(myXlDown).Row, workSheet.Range("B2").End(myXlToRight).Column)).Address
    chart.PlotBy = myXlColumns
    
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = myXlPrimary
    Next serie
    
    chart.HasAxis(myXlValue, myXlSecondary) = True
    
    For i = 1 To 2
        chart.SeriesCollection(i).AxisGroup = myXlSecondary
    Next
    
    chart.chartData.Workbook.Close
End Sub

Private Sub ShowWorkbook(chartData As Variant)
    On Error GoTo ActivateWorkbook
     If IsEmpty(chartData.Workbook) Then
        chartData.Activate
    End If
    Exit Sub
ActivateWorkbook:
    chartData.Activate
End Sub

Sub UpdateGraphics()
Attribute UpdateGraphics.VB_Description = "Applique le style Normal et convertit les titres sélectionnés en corps de texte."
Attribute UpdateGraphics.VB_ProcData.VB_Invoke_Func = "TemplateProject.NewMacros.AbaisserEnCorpsDeTexte"
'
' AbaisserEnCorpsDeTexte Macro
' Applique le style Normal et convertit les titres sélectionnés en corps de texte.
'
    'Set charts = FindChart()
    Call Initialisation
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            Call ShowWorkbook(shape.chart.chartData)
            Select Case shape.chart.chartData.Workbook.Sheets(1).name
                Case "ALEByAsset", "ALEByAssetType", "ALEByScenario", "ALEByScenarioType"
                    UpdateALE (shape.chart)
                 Case "Compliance27001", "Compliance27002"
                    UpdateCompliance (shape.chart)
                Case "Budget"
                    UpdateBudget (shape.chart)
                Case "EvolutionOfProfitability"
                    UpdateEvolutionOfProfitability (shape.chart)
            End Select
        End If
    Next shape
End Sub
