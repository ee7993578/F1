/*
  Exact source extractor for dbo.fee_defaulter_report.
  Target: localhost / dpsbsr_edufox_net
  Run in SSMS. It returns the complete CREATE PROCEDURE definition stored in SQL Server.
*/
USE [dpsbsr_edufox_net];
GO

SET NOCOUNT ON;

IF OBJECT_ID(N'dbo.fee_defaulter_report', N'P') IS NULL
BEGIN
    THROW 50000, 'dbo.fee_defaulter_report was not found in this database.', 1;
END;
GO

SELECT
    QUOTENAME(OBJECT_SCHEMA_NAME(object_id)) + N'.' + QUOTENAME(OBJECT_NAME(object_id)) AS procedure_name,
    modify_date,
    definition AS procedure_script
FROM sys.sql_modules
WHERE object_id = OBJECT_ID(N'dbo.fee_defaulter_report', N'P');
GO

/* Alternative SSMS command: */
-- EXEC sys.sp_helptext N'dbo.fee_defaulter_report';
