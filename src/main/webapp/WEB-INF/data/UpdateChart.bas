Attribute VB_Name = "NewMacros"
Private Function FindChart() As Scripting.dictionary
    Dim dictionary2 As Scripting.dictionary
    Dim shape As InlineShape
    Set dictionary2 = New Scripting.dictionary
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            dictionary2.Add shape.chart.ChartData.workbook.Sheets(1).Name, shape.chart
        End If
    Next shape
    Set FindChart = dictionary2
End Function

Private Sub UpdateALEByAsset(mychart)
    Dim chart As chart
    Dim workSheet As Excel.workSheet
    Dim lastNotEmptyRow As Integer
    Set chart = mychart
    Set workSheet = chart.ChartData.workbook.Sheets(1)
    lastNotEmptyRow = workSheet.Range("A" & workSheet.Rows.Count).End(xlUp).Row
    chart.SetSourceData Source:= _
        workSheet.Range("A1:E5")
   
End Sub
Private Sub UpdateALEByAssetType(mychart)

End Sub
Private Sub UpdateALEByScenario(mychart)

End Sub
Private Sub UpdateALEByScenarioType(mychart)

End Sub
Private Sub UpdateCompliance27001(mychart)
'DernCol = Cells(1, Cells.Columns.Count).End(xlToLeft).Column

End Sub
Private Sub UpdateCompliance27002(mychart)

End Sub
Private Sub UpdateBudget(mychart)

End Sub
Private Sub UpdateEvolutionOfProfitability(mychart)

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
            Case "ALEByAsset"
                UpdateALEByAsset (charts(chartName))
            Case "ALEByAssetType"
                UpdateALEByAssetType (charts(chartName))
            Case "ALEByScenario"
                UpdateALEByScenario (charts(chartName))
            Case "ALEByScenarioType"
                UpdateALEByScenarioType (charts(chartName))
             Case "Compliance27001"
                UpdateCompliance27001 (charts(chartName))
            Case "Compliance27002"
                UpdateCompliance27002 (charts(chartName))
            Case "Budget"
                UpdateBudget (charts(chartName))
            Case "EvolutionOfProfitability"
                UpdateEvolutionOfProfitability (charts(chartName))
        End Select
    Next chartName
End Sub
