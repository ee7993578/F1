import { DefineMarksPrePrimary } from './ExamWorkExact'

// The legacy menu exposes this page as Preprimary_marks_mapping_rmps.aspx.
// It uses the same class-wise/student-wise primary marks workflow, with the
// legacy Primary (Junior in this database) class scope selected by the API.
export default function PreprimaryMarksMapping() {
  return <DefineMarksPrePrimary mapping />
}
