import { useState } from "react"

import { NavLink } from "react-router-dom"
import { Bullseye, Button, Modal, Spinner } from "@patternfly/react-core"
import { ArrowRightIcon } from "@patternfly/react-icons"
import { TableComposable, Thead, Tbody, Tr, Th, Td } from "@patternfly/react-table"

import { formatDateTime } from "../../utils"
import ConfirmDeleteModal from "../../components/ConfirmDeleteModal"
import Api, { TableReportSummary } from "../../api"

type ListReportsModalProps = {
    isOpen: boolean
    onClose(): void
    summary?: TableReportSummary
    onReload(): void
}

export default function ListReportsModal(props: ListReportsModalProps) {
    const [deleteId, setDeleteId] = useState<number>()
    return (
        <Modal
            title={"Reports for " + props?.summary?.title}
            variant="small"
            isOpen={props.isOpen}
            onClose={props.onClose}
        >
            <div style={{ overflowY: "auto", minHeight: "40vh", maxHeight: "60vh" }}>
                {!props.summary && (
                    <Bullseye>
                        <Spinner size="xl" />
                    </Bullseye>
                )}
                {props.summary && (
                    <TableComposable variant="compact">
                        <Thead>
                            <Tr>
                                <Th>Report</Th>
                                <Th>Created</Th>
                                <Th>Actions</Th>
                            </Tr>
                        </Thead>
                        <Tbody>
                            {props.summary.reports.map(({ id, created }) => (
                                <Tr key={id}>
                                    <Td>
                                        <NavLink to={`/reports/table/${id}`}>
                                            <ArrowRightIcon />
                                            {"\u00A0"}
                                            {id}
                                        </NavLink>
                                    </Td>
                                    <Td>{formatDateTime(created * 1000)}</Td>
                                    <Td>
                                        <Button onClick={() => setDeleteId(id)} variant="danger">
                                            Delete
                                        </Button>
                                    </Td>
                                </Tr>
                            ))}
                        </Tbody>
                    </TableComposable>
                )}
            </div>
            <ConfirmDeleteModal
                isOpen={deleteId !== undefined}
                onClose={() => setDeleteId(undefined)}
                description={"report " + deleteId}
                onDelete={() => {
                    if (!deleteId) {
                        return Promise.resolve()
                    }
                    return Api.reportServiceDeleteTableReport(deleteId).then(() => props.onReload())
                }}
            />
        </Modal>
    )
}
