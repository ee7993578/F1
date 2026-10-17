// These legacy "Class" selectors bind TB_Class.sec_id, not class_name.id.
export function studentSectionOptions(sections = [], orderById = false) {
  return sections.map(row => ({
    id: String(row.sectionId),
    name: `${row.className || ''} (${row.sectionName || ''})`,
    classId: row.classId,
    className: row.className,
    sectionName: row.sectionName,
  })).sort((a, b) => Number(a.classId) - Number(b.classId)
    || (orderById ? Number(a.id) - Number(b.id) : String(a.sectionName).localeCompare(String(b.sectionName))))
}
