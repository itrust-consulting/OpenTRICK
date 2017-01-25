Attribute VB_Name = "UpdateChart"
Dim myxlUp As Variant
Dim myXlDown As Variant
Dim myXlColumns As Variant
Dim myxlLineMarkers As Variant
Dim myxlColumnClustered As Variant
Dim myXlValue As Variant
Dim myXlPrimary As Variant
Dim myXlSecondary As Variant
Dim myXlToRight As Variant
Dim myXLColumnStacked100 As Variant
Dim myXLShiftUp As Integer
Dim myXLToLeft As Integer
Private Sub Initialisation()
    myxlUp = -4162
    myXlDown = -4121
    myXlColumns = 2
    myXlValue = 2
    myXlPrimary = 1
    myXlSecondary = 2
    myXlToRight = -4161
    myxlLineMarkers = 65
    myxlColumnClustered = 51
    myXLColumnStacked100 = 53
    myXLShiftUp = -4162
    myXLToLeft = -4159
End Sub
Public Function EndsWith(str As String, ending As String) As Boolean
     Dim endingLen As Integer
     endingLen = Len(ending)
     EndsWith = (Right(Trim(UCase(str)), endingLen) = UCase(ending))
End Function

Public Function isTemplate() As Boolean
    isTemplate = EndsWith(ThisDocument.name, ".dotm")
End Function

Public Function StartsWith(str As String, start As String) As Boolean
     Dim startLen As Integer
     startLen = Len(start)
     StartsWith = (Left(Trim(UCase(str)), startLen) = UCase(start))
End Function

Public Function Ceiling(ByVal X As Double, Optional ByVal Factor As Double = 1) As Double
    ' X is the value you want to round
    ' is the multiple to which you want to round
    Ceiling = (Int(X / Factor) - (X / Factor - Int(X / Factor) > 0)) * Factor
End Function

Public Function Floor(ByVal X As Double, Optional ByVal Factor As Double = 1) As Double
    ' X is the value you want to round
    ' is the multiple to which you want to round
    Floor = Int(X / Factor) * Factor
End Function

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

Private Sub UpdateChartName()
    If Not isTemplate Then
        Exit Sub
    End If
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            Call ShowWorkbook(shape.chart.chartData)
            shape.AlternativeText = shape.chart.chartData.Workbook.Sheets(1).name
            shape.chart.chartData.Workbook.Close
        End If
   Next shape
End Sub


Function HexToLongRGB(sHexVal As String) As Long
    Dim lRed As Long
    Dim lGreen As Long
    Dim lBlue As Long
    lRed = CLng("&H" & Left$(sHexVal, 2))
    lGreen = CLng("&H" & Mid$(sHexVal, 3, 2))
    lBlue = CLng("&H" & Right$(sHexVal, 2))
    HexToLongRGB = RGB(lRed, lGreen, lBlue)
End Function

Private Sub UpdateALE(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    lastNotEmptyRow = workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row
    workSheet.Range("B2:B" & lastNotEmptyRow).NumberFormat = "[>9.99]# ### ### ### ##0kï¿½;[>0.01]#0.0kï¿½;0kï¿½"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range("A1:B" & lastNotEmptyRow).Address
    chart.PlotBy = myXlColumns
End Sub

Private Sub UpdateRisk(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    Dim lastColumn As Integer
    Dim serie As Series
    Dim values As Variant
    Dim i As Integer
    Dim j As Integer
    
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    lastColumn = workSheet.Range("B2").End(myXlToRight).Column
    lastNotEmptyRow = workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range("A2:" & workSheet.Cells(lastNotEmptyRow, lastColumn).Address).Address
    chart.PlotBy = myXLColumnStacked100
    For i = 2 To lastColumn
        With chart.SeriesCollection(i - 1)
            .Interior.Color = HexToLongRGB(workSheet.Cells(1, i).Value)
            values = .values
            For j = LBound(values) To UBound(values)
                If values(j) = 0 Then
                    With .Points(j)
                        If .HasDataLabel Then
                            .DataLabel.Delete
                        End If
                    End With
                End If
            Next j
        End With
    Next i
End Sub
Private Sub UpdateCompliance(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    If Not IsEmpty(workSheet.Cells(1, 1)) Then
        workSheet.Range(workSheet.Cells(2, 2), workSheet.Cells(workSheet.Range("A1").End(myXlDown).row, workSheet.Range("B1").End(myXlToRight).Column)).NumberFormat = "0%"
        chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A1").End(myXlDown).row, workSheet.Range("B1").End(myXlToRight).Column)).Address
        chart.Axes(xlValue).TickLabels.NumberFormat = "0%"
        chart.Axes(xlValue).MaximumScale = 1
        chart.Axes(xlValue).MinimumScale = 0
        chart.PlotBy = myXlColumns
    End If
End Sub
Private Sub UpdateBudget(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    workSheet.Range("B2:E" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyManDay
    workSheet.Range("F2:H" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "### ### ### ###0kï¿½"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(myXlDown).row, workSheet.Range("B2").End(myXlToRight).Column)).Address
    chart.PlotBy = myXlColumns
    'Move all series to primary axis
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = xlPrimary
    Next serie
    
    chart.HasAxis(myXlValue, myXlSecondary) = True
    
    For i = 1 To 4
        chart.SeriesCollection(i).AxisGroup = myXlSecondary
    Next
End Sub
Private Sub UpdateEvolutionOfProfitability(mychart)
    Dim chart As chart
    Dim workSheet As Variant
    Dim lastNotEmptyRow As Integer
    
    Set chart = mychart
    Set workSheet = chart.chartData.Workbook.Sheets(1)
    workSheet.Range("B2:C" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "0%"
    workSheet.Range("D2:D" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "### ### ### ###0kï¿½"
    workSheet.Range("E2:G" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "### ### ### ###0" & ThisDocument.CurrencyKiloEuroPerYear
    workSheet.Range("H2:H" & workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row).NumberFormat = "### ### ### ###0"
    chart.SetSourceData Source:="'" & workSheet.name & "'!" & workSheet.Range(workSheet.Cells(1, 1), workSheet.Cells(workSheet.Range("A2").End(myXlDown).row, workSheet.Range("B2").End(myXlToRight).Column)).Address
    chart.PlotBy = myXlColumns
    
    For Each serie In chart.SeriesCollection
        serie.AxisGroup = myXlPrimary
    Next serie
    
    chart.HasAxis(myXlValue, myXlSecondary) = True
    
    For i = 1 To 2
        chart.SeriesCollection(i).AxisGroup = myXlSecondary
        chart.SeriesCollection(i).ChartType = myxlColumnClustered
    Next
    
    For i = 3 To 6
        chart.SeriesCollection(i).ChartType = xlLineMarkers
    Next
    
End Sub

Private Sub ShowWorkbook(chartData As Variant)
    On Error GoTo ActivateWorkbook
     If IsEmpty(chartData.Workbook) Then
        chartData.Workbook.Application.WindowState = -4140
    End If
    Exit Sub
ActivateWorkbook:
    chartData.Activate
    chartData.Workbook.Application.WindowState = -4140
End Sub
Private Sub ActivateWorkbook(chartData As Variant)
    chartData.Activate
    chartData.Workbook.Application.WindowState = -4140
End Sub
Private Function CountChart(name As String) As Integer
    Dim count As Integer: count = 0
    
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart And shape.AlternativeText = name Then
            count = count + 1
        End If
    Next
    CountChart = count
End Function

Private Function BestDiviser(numnerator As Integer, maxDiviser As Integer) As Integer
    Dim miniDiviser As Integer: miniDiviser = maxDiviser
    Dim minMod As Integer: minMod = numnerator
    Dim auxMod As Integer: Dim divValue As Integer
    
    For i = maxDiviser To maxDiviser / 3 Step -1
        auxMod = numnerator Mod i
        If auxMod < minMod Then
            minMod = auxMod
            miniDiviser = i
        End If
    Next
    BestDiviser = miniDiviser
End Function

Private Sub DistributionALEChart(chartName As String, chartCount As Integer, totalRow As Integer)
    Dim workSheet As Variant
    Dim rowCount As Integer: rowCount = totalRow \ chartCount
    Dim chartIndex As Integer: chartIndex = 0
    
    If chartCount < 2 Then
        Exit Sub
    End If
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart And shape.AlternativeText = chartName Then
            Call ShowWorkbook(shape.chart.chartData)
            Set workSheet = shape.chart.chartData.Workbook.Sheets(1)
            If chartIndex > 0 Then
                workSheet.Range("A2:B" & ((rowCount * chartIndex) + 1)).Delete
            End If
            workSheet.Range("A" & (rowCount + 2) & ":B" & totalRow + 1).Delete
            chartIndex = chartIndex + 1
            workSheet.name = chartName & chartIndex
        End If
    Next shape
End Sub

Private Sub DistributionRiskChart(chartName As String, chartCount As Integer, totalRow As Integer)
    Dim workSheet As Variant
    Dim rowCount As Integer: rowCount = Floor(totalRow * 1# \ chartCount * 1#)
    Dim chartIndex As Integer: chartIndex = 0
    Dim columnCount As Integer: columnCount = 0
    If chartCount < 2 Then
        Exit Sub
    End If
    Debug.Print chartCount
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart And shape.AlternativeText = chartName Then
            Call ActivateWorkbook(shape.chart.chartData)
            Set workSheet = shape.chart.chartData.Workbook.Sheets(1)
            If chartIndex = 0 Then
                columnCount = workSheet.Range("B2").End(myXlToRight).Column
            Else:
                workSheet.Range("A3:" & workSheet.Cells(((rowCount * chartIndex) + 2), columnCount).Address).Delete
            End If
            workSheet.Range(workSheet.Range("A" & (rowCount + 3) & ":" & workSheet.Cells(totalRow + 2, columnCount).Address).Address).Delete
            chartIndex = chartIndex + 1
            workSheet.name = chartName & chartIndex
        End If
    Next shape
End Sub

Private Sub SplitChart(chartName As String, size As Integer, maxSize As Integer)
    Dim workSheet As Variant
    Dim totalRow As Integer
    Dim auxDim As Integer
    Dim rowCount As Integer: rowCount = 1
    Dim chartCount As Integer: chartCount = 1
    Dim lastCopy As Variant
    Dim copy As Variant
    If CountChart(chartName) > 1 Then
        Exit Sub
    End If
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart And shape.AlternativeText = chartName Then
            Call ShowWorkbook(shape.chart.chartData)
            Set workSheet = shape.chart.chartData.Workbook.Sheets(1)
            If StartsWith(chartName, "Risk") Then
                totalRow = workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row - 2
            Else
                totalRow = workSheet.Range("A" & workSheet.Rows.count).End(myxlUp).row - 1
            End If
            If totalRow > size Then
                chartCount = Ceiling(totalRow * 1# / size * 1#)
                If totalRow > maxSize Then
                    For i = 2 To chartCount
                        shape.chart.Select
                        Selection.copy
                        Selection.PasteAndFormat (wdFormatOriginalFormatting)
                        Selection.TypeParagraph
                    Next i
                End If
            End If
            Exit For
        End If
    Next shape
    If StartsWith(chartName, "Risk") Then
        Call DistributionRiskChart(chartName, chartCount, totalRow)
    Else
        Call DistributionALEChart(chartName, chartCount, totalRow)
    End If
End Sub

Public Sub CloseWorkbooks()
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
        On Error GoTo HasError
            shape.chart.chartData.Workbook.Close
        End If
HasError:
    Next
End Sub


Sub UpdateGraphics()
Attribute UpdateGraphics.VB_Description = "Applique le style Normal et convertit les titres sï¿½lectionnï¿½s en corps de texte."
Attribute UpdateGraphics.VB_ProcData.VB_Invoke_Func = "TemplateProject.NewMacros.AbaisserEnCorpsDeTexte"
'Update chart
'Mise à jour des graphs
'
    Call Initialisation
    Call SplitChart("ALEByAsset", 10, 12)
    Call SplitChart("ALEByScenario", 10, 12)
    Call SplitChart("RiskByAsset", 10, 12)
    Call SplitChart("RiskByScenario", 10, 12)
    For Each shape In ActiveDocument.InlineShapes
        If shape.HasChart Then
            Call ShowWorkbook(shape.chart.chartData)
            Select Case shape.AlternativeText
                Case "ALEByAsset", "ALEByAssetType", "ALEByScenario", "ALEByScenarioType"
                    UpdateALE (shape.chart)
                Case "RiskByScenario", "RiskByScenarioType", "RiskByAsset", "RiskByAssetType"
                      UpdateRisk (shape.chart)
                 Case "Compliance27001", "Compliance27002"
                    UpdateCompliance (shape.chart)
                Case "Budget"
                    UpdateBudget (shape.chart)
                Case "EvolutionOfProfitability"
                    UpdateEvolutionOfProfitability (shape.chart)
            End Select
        End If
    Next shape
    Call CloseWorkbooks
End Sub
